package ga.asev.schedule;

import ga.asev.dao.UserSerialDao;
import ga.asev.model.UserSerial;
import ga.asev.service.NyaaCrawlerService;
import ga.asev.service.UserSerialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    UserSerialService userSerialService;

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
        return userSerialService.downloadUserSerial(userSerial);
    }
}
