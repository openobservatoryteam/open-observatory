package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.util.Objects;
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
  private String image;

  @Column(columnDefinition = "INTEGER", nullable = false)
  private Integer validityTime;

  // ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CelestialBodyEntity that = (CelestialBodyEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
