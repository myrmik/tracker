package ga.asev.service;

public interface NotificationService {
    /**
     * @return The number of unread notifications for the current user.
     */
    int getUnreadNotificationsCount();
}
