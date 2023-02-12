package fr.openobservatory.backend;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "celestial_body")
public class CelestialBodyDto {

    @Id
    @SequenceGenerator(name = "id", sequenceName = "celestial_body_id_sequence")
    @GeneratedValue(strategy = GenerationType.UUID, generator = "celestial_body_id_sequence")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false)
    private String imageURL;

    @Column(name = "validity_time", nullable = false)
    private Integer validityTime;

    public CelestialBodyDto(UUID id, String name, String imageURL, Integer validityTime) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.validityTime = validityTime;
    }

    public CelestialBodyDto(){
    }

    // GETTERS
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Integer getValidityTime() {
        return validityTime;
    }

    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String  imageURL) {
        this.imageURL = imageURL;
    }

    public void setValidityTime(Integer validityTime) {
        this.validityTime = validityTime;
    }

    // TOOLS

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CelestialBodyDto that = (CelestialBodyDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(imageURL, that.imageURL) && Objects.equals(validityTime, that.validityTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageURL, validityTime);
    }

    @Override
    public String toString() {
        return "CelestialBodyDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", validityTime=" + validityTime +
                '}';
    }
}
