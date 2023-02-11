package fr.openobservatory.backend;

import jakarta.persistence.*;

@Entity
@Table(name = "celestial_body")
public class CelestialBody {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

}
