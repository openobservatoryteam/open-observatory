package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "observation_vote")
public class ObservationVoteEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "observation_id", nullable = false)
  private ObservationEntity observation;

  @Column(name = "vote", nullable = false)
  private VoteType vote;

  @AllArgsConstructor
  @Getter
  public enum VoteType {
    UPVOTE(1),
    DOWNVOTE(-1);

    // ---

    private final int weight;
  }
}
