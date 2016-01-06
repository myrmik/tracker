package ga.asev.model;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="SERIAL_COMMENT")
public class SerialComment implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String author;

    @Column
    private String content;

    @Column
    private LocalDateTime publishDate;

    @ManyToOne
    @JoinColumn(name = "SERIAL_INFO_ID")
    private SerialInfo serialInfo;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
