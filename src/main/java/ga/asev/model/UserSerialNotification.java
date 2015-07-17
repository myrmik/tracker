package ga.asev.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_SERIAL_NOTIFICATION")
public class UserSerialNotification implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private NotificationType type;

    @Column
    private int episode;

    @Column
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "USER_SERIAL_ID")
    private UserSerial userSerial;

    @Override
    public String toString() {
        return type.toString(userSerial, episode);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UserSerial getUserSerial() {
        return userSerial;
    }

    public void setUserSerial(UserSerial userSerial) {
        this.userSerial = userSerial;
    }
}
