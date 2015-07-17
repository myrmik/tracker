package ga.asev.model;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="SERIAL")
public class Serial implements Serializable {

    public Serial() {
    }

    public Serial(String name, LocalDateTime publishDate) {
        this.name = name;
        this.publishDate = publishDate;
    }

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String name;

    @Column
    private LocalDateTime publishDate;

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

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
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
