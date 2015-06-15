package ga.asev.model;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="SERIAL")
public class Serial {

    public Serial() {
    }

    public Serial(String name, LocalDateTime lastUpdated) {
        this.name = name;
        this.lastUpdated = lastUpdated;
    }

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String name;

    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;

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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
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
