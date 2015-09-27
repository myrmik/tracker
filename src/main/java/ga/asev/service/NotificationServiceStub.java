package ga.asev.service;

import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
public class NotificationServiceStub implements NotificationService {
    @Override
    public int getUnreadNotificationsCount() {
        return 0;
    }
}
