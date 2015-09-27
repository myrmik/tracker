package ga.asev.dao;

import ga.asev.model.UserSerialNotification;

import java.util.List;

public interface UserSerialNotificationDao {
    UserSerialNotification insertNotification(UserSerialNotification notification);
    List<UserSerialNotification> selectAllNotifications();
    void deleteNotification(int id);
    int selectUnreadCount();
}
