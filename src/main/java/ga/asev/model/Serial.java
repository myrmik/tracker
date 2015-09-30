package ga.asev.model;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name="SERIAL")
public class Serial implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String name;

    @Column
    private int publishEpisode;

    @Column
    private LocalDateTime publishDate;

    @OneToOne(cascade = ALL, orphanRemoval = true, mappedBy="serial")
    private SerialInfo serialInfo;

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

    public int getPublishEpisode() {
        return publishEpisode;
    }

    public void setPublishEpisode(int publishEpisode) {
        this.publishEpisode = publishEpisode;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public SerialInfo getSerialInfo() {
        return serialInfo;
    }

    public void setSerialInfo(SerialInfo serialInfo) {
        this.serialInfo = serialInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Serial serial = (Serial) o;

        return !(name != null ? !name.equals(serial.name) : serial.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
