package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;

@Data
@Entity
@Table(name = "observation")
public class ObservationEntity implements Observation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "FLOAT", nullable = false)
  private Double latitude;

  @Column(columnDefinition = "FLOAT", nullable = false)
  private Double longitude;

  @Column(columnDefinition = "INTEGER", nullable = false)
  private Integer orientation;

  @ManyToOne
  @JoinColumn(name = "celestial_body_id")
  private CelestialBodyEntity celestialBody;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private UserEntity author;

  @Column(columnDefinition = "SMALLINT", nullable = false)
  private Visibility visibility;

  @OneToMany(mappedBy = "observation")
  private Set<ObservationVoteEntity> votes;

  @Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
  private Instant createdAt;

  // ---

  public ObservationVoteEntity.VoteType getCurrentVote() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return null;
    var vote =
        getVotes().stream()
            .filter(v -> v.getUser().getUsername().equalsIgnoreCase(authentication.getName()))
            .findFirst();
    return vote.map(ObservationVoteEntity::getVote).orElse(null);
  }

  public int getKarma() {
    return getVotes().stream().map(vote -> vote.getVote().getWeight()).reduce(0, Integer::sum);
  }

  public boolean hasExpired() {
    return createdAt
        .plus(celestialBody.getValidityTime(), ChronoUnit.HOURS)
        .isBefore(Instant.now());
  }

  // ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ObservationEntity that = (ObservationEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  // ---

  public enum Visibility {
    CLEARLY_VISIBLE,
    VISIBLE,
    SLIGHTLY_VISIBLE,
    BARELY_VISIBLE
  }
}
