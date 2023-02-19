package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "celestial_body")
public class CelestialBodyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "VARCHAR(64)", nullable = false, unique = true)
  private String name;

  @Column(columnDefinition = "TEXT", nullable = false)
  @Lob
  private String image;

  @Column(columnDefinition = "INTEGER", nullable = false)
  private Integer validityTime;
}
