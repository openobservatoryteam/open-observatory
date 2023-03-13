package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

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

  public enum VoteType {
    UPVOTE,
    DOWNVOTE
  }
}
