package fr.openobservatory.backend;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "celestial_body")
public class CelestialBodyDto {

    @Id
    @SequenceGenerator(name = "id", sequenceName = "celestial_body_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "celestial_body_id_sequence")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false)
    private Byte[] image;

    @Column(name = "validity_time", nullable = false)
    private Integer validityTime;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public CelestialBodyDto(Integer id, String name, Byte[] image, Integer validityTime, Date createdAt) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.validityTime = validityTime;
        this.createdAt = createdAt;
    }

    public CelestialBodyDto(){
    }

    // GETTERS
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Byte[] getImage() {
        return image;
    }

    public Integer getValidityTime() {
        return validityTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(Byte[] image) {
        this.image = image;
    }

    public void setValidityTime(Integer validityTime) {
        this.validityTime = validityTime;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CelestialBodyDto that = (CelestialBodyDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Arrays.equals(image, that.image) && Objects.equals(validityTime, that.validityTime) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, validityTime, createdAt);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "CelestialBody{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image=" + Arrays.toString(image) +
                ", validityTime=" + validityTime +
                ", createdAt=" + createdAt +
                '}';
    }
}
