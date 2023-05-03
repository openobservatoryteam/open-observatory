package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import lombok.*;
import lombok.Builder.Default;
import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "\"user\"")
public class UserEntity {

  public static final String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9_]{0,31}$";

  // ---

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // --- Credentials

  @Column(length = 32, nullable = false, unique = true, updatable = false)
  private String username;

  @Column(length = 72, nullable = false)
  private String password;

  @Column(nullable = false)
  @Default
  private Type type = Type.USER;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  // --- Profile information

  @Column
  @Default
  private Long avatarId;

  @Column(length = 500)
  private String biography;

  @Column(nullable = false)
  @Default
  private boolean isPublic = true;

  // --- Last position

  @Column private Double latitude;

  @Column private Double longitude;

  @Column private Instant positionAt;

  // --- Notification settings

  @Column(nullable = false)
  @Default
  private Integer notificationRadius = 5;

  @Column(nullable = false)
  private boolean notificationEnabled;

  // ---

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
  @Default
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<UserAchievementEntity> achievements = Set.of();

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "author")
  @Default
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ObservationEntity> observations = Set.of();

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
  @Default
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ObservationVoteEntity> votes = Set.of();

  // ---

  public enum Type {
    USER,
    ADMIN
  }
}
