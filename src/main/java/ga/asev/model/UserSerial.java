package ga.asev.model;


import javax.persistence.*;
import java.time.LocalDateTime;

import static ga.asev.util.DateUtil.formatPeriodTillNowTo;
import static ga.asev.util.DateUtil.getProgressBetween;
import static java.time.LocalDateTime.now;

@Entity
@Table(name="USER_SERIAL")
public class UserSerial {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String name;

    @Column
    private int episode;

    @Column
    private LocalDateTime lastUpdated;

    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="SERIAL_ID")
    private Serial serial;

    public String getTimeLeft() {
        if (getPublishDate() == null) return null;
        if (expectedNextEpisodeDate().isBefore(now())) return null;
        return formatPeriodTillNowTo(expectedNextEpisodeDate());
    }

    public Double getTimeLeftProgress() {
        if (getPublishDate() == null) return null;
        if (expectedNextEpisodeDate().isBefore(now())) return 1d;
        return getProgressBetween(getPublishDate(), expectedNextEpisodeDate());
    }

    private LocalDateTime expectedNextEpisodeDate() {
        return getPublishDate() == null ? null : getPublishDate().plusWeeks(1);
    }

    @Override
    public String toString() {
        return getName() + " - " + getEpisodeString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEpisode() {
        return episode;
    }

    public String getEpisodeString() {
        if (episode < 10)
            return "0" + Integer.toString(episode);
        return Integer.toString(episode);
    }

    public void setEpisodeString(String episodeStr) {
        int episode;
        try {
            episode = Integer.parseInt(episodeStr);
        } catch (NumberFormatException e) {
            return;
        }
        setEpisode(episode);
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

    public LocalDateTime getPublishDate() {
        return serial.getPublishDate();
    }

    public void setPublishDate(LocalDateTime publishDate) {
        serial.setPublishDate(publishDate);
    }

    public Serial getSerial() {
        return serial;
    }

    public void setSerial(Serial serial) {
        this.serial = serial;
    }

    public String getOriginalName() {
        return serial.getName();
    }
}
