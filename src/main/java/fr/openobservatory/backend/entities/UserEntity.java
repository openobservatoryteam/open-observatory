package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name = "\"user\"")
public class UserEntity {

  public static final String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9_]{0,31}$";

  // ---

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "VARCHAR(32)", nullable = false, unique = true, updatable = false)
  private String username;

  @Column(columnDefinition = "VARCHAR(60)", nullable = false)
  private String password;

  @Column(columnDefinition = "TEXT")
  private String avatar;

  @Column(columnDefinition = "TEXT")
  private String biography;

  @Column(columnDefinition = "BOOLEAN")
  private boolean isPublic;

  @Column(columnDefinition = "SMALLINT")
  private Type type;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  // ---

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "author")
  private Set<ObservationEntity> observations;

  // ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return username.equalsIgnoreCase(that.username);
  }

  @Override
  public int hashCode() {
    return username.hashCode();
  }

  // ---

  public enum Type {
    USER,
    ADMIN
  }
}
