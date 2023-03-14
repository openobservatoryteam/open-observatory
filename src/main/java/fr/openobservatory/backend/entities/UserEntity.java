package fr.openobservatory.backend.entities;

import fr.openobservatory.backend.dto.AchievementDto;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
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

  @Column(columnDefinition = "BYTEA")
  private String avatar;

  @Column(columnDefinition = "TEXT")
  private String biography;

  @Column(columnDefinition = "BOOLEAN")
  private boolean isPublic;

  @Column(columnDefinition = "SMALLINT")
  private Type type;

  @Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
  private Instant createdAt;

  // ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  // ---

  public enum Type {
    USER,
    ADMIN
  }

  public int getKarma() {
    return 0;
  }

  public List<AchievementDto> getAchievements() {
    return List.of();
  }
}
