package ga.asev.schedule;

import ga.asev.event.DownloadEvent;
import ga.asev.dao.UserSerialDao;
import ga.asev.dao.UserSerialNotificationDao;
import ga.asev.model.NotificationType;
import ga.asev.model.UserSerial;
import ga.asev.model.UserSerialNotification;
import ga.asev.service.NyaaCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static ga.asev.util.ThreadUtil.sleepForDownload;

@Component
public class TorrentSchedule extends BaseSchedule {

    private final static long UPDATE_SERIALS_DELAY = 4 * 60 * 60 * 1000;    // 4h
    private final static long DOWNLOAD_DELAY = 30 * 60 * 1000;              // 30m

    @Autowired
    NyaaCrawlerService nyaaCrawlerService;

    @Autowired
    UserSerialDao userSerialDao;

    @Autowired
    UserSerialNotificationDao userSerialNotificationDao;

    @Autowired
    DownloadEvent downloadEvent;

    @Scheduled(fixedDelay=UPDATE_SERIALS_DELAY)
    public void updateSerials() {
        nyaaCrawlerService.updateSerialList();
    }

    @Scheduled(fixedDelay=DOWNLOAD_DELAY)
    public void searchTorrents() {
        List<UserSerial> userSerials = userSerialDao.selectAllUserSerials();
        if (userSerials == null) {
            log.info("There is no torrents to download");
            return;
        }

        log.info("Started search for torrents, size: " + userSerials.size());
        int downloadedNbr = 0;
        for (UserSerial userSerial : userSerials) {
            downloadedNbr += downloadTorrent(userSerial);
            sleepForDownload();
            log.info("Episodes were downloaded: " + downloadedNbr + ", last: " + userSerial);
        }
        log.info("Finished search for torrents, size: " + userSerials.size() + ", downloaded: " + downloadedNbr);
    }

    private int downloadTorrent(UserSerial userSerial) {
        int downloaded = nyaaCrawlerService.downloadTorrents(userSerial);
        if (downloaded > 0) {
            userSerial.setEpisode(userSerial.getEpisode() + downloaded); // increase # of episode
            userSerial.setLastUpdated(LocalDateTime.now());
            userSerialDao.insertUserSerial(userSerial);
            notifyDownloaded(userSerial, downloaded);
            return downloaded;
        }
        log.info("There is no next episode for: " + userSerial);
        return 0;
    }

    private void notifyDownloaded(UserSerial userSerial, int downloaded) {
        for (int i = userSerial.getEpisode() - downloaded + 1; i < userSerial.getEpisode(); i++) {
            UserSerialNotification notification = new UserSerialNotification();
            notification.setType(NotificationType.EPISODE_DOWNLOADED);
            notification.setEpisode(i);
            notification.setLastUpdated(LocalDateTime.now());
            notification.setUserSerial(userSerial);
            userSerialNotificationDao.insertNotification(notification);
            downloadEvent.notifyObservers(notification);
        }
    }
}
