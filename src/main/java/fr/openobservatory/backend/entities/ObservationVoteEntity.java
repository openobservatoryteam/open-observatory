package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "observation_vote")
public class ObservationVoteEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @ManyToOne(optional = false)
  private ObservationEntity observation;

  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private VoteType vote;

  // ---

  @AllArgsConstructor
  @Getter
  public enum VoteType {
    UPVOTE(1),
    DOWNVOTE(-1);

    // ---

    private final int weight;
  }
}
