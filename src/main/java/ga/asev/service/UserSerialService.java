package ga.asev.service;

import ga.asev.dao.UserSerialDao;
import ga.asev.dao.UserSerialNotificationDao;
import ga.asev.event.DownloadEvent;
import ga.asev.model.NotificationType;
import ga.asev.model.UserSerial;
import ga.asev.model.UserSerialNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserSerialService extends BaseService {

    @Autowired
    NyaaCrawlerService nyaaCrawlerService;

    @Autowired
    UserSerialDao userSerialDao;

    @Autowired
    UserSerialNotificationDao userSerialNotificationDao;

    @Autowired
    DownloadEvent downloadEvent;

    public void deleteUserSerial(UserSerial userSerial) {
//        userSerial.getNotifications().clear();
        userSerialDao.deleteUserSerial(userSerial.getId());
    }

    public int downloadUserSerial(UserSerial userSerial) {
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
        for (int i = userSerial.getEpisode() - downloaded + 1; i <= userSerial.getEpisode(); i++) {
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
