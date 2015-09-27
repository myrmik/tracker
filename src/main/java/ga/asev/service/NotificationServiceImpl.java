package ga.asev.service;

import com.vaadin.spring.annotation.SpringComponent;
import ga.asev.dao.UserSerialNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    UserSerialNotificationDao userSerialNotificationDao;

    @Override
    public int getUnreadNotificationsCount() {
        return userSerialNotificationDao.selectUnreadCount();
    }
}
