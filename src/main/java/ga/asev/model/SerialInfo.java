package ga.asev.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name="SERIAL_INFO")
public class SerialInfo implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String name;

    @Column
    private String genre;

    @Column
    private int size;

    @Column
    private int duration;

    @Column
    private String posterUrl;

    @Column
    private String companyLogoUrl;

    @Column
    private String summary;

    @OneToOne(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name="SERIAL_ID")
    private Serial serial;

    @OneToMany(fetch=FetchType.LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn(name="SERIAL_INFO_ID")
    private List<SerialComment> comments;

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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public void setCompanyLogoUrl(String companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public Serial getSerial() {
        return serial;
    }

    public void setSerial(Serial serial) {
        this.serial = serial;
    }

    public List<SerialComment> getComments() {
        return comments;
    }

    public void setComments(List<SerialComment> comments) {
        if (comments != null) {
            for (SerialComment comment : comments) {
                comment.setSerialInfo(this);
            }
        }

        this.comments = comments;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
