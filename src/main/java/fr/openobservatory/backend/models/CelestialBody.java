package fr.openobservatory.backend.models;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "celestial_body")
public class CelestialBody {

    @Id
    @SequenceGenerator(name = "id", sequenceName = "celestial_body_id_sequence")
    @GeneratedValue(strategy = GenerationType.UUID, generator = "celestial_body_id_sequence")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "validity_time", nullable = false)
    private Integer validityTime;

}
