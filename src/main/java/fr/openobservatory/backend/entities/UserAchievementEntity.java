package fr.openobservatory.backend.entities;

import fr.openobservatory.backend.repositories.Achievements;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@Entity
@Table(name = "user_achievement")
@Accessors(chain = true)
public class UserAchievementEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column
  private Achievements.Achievement achievement;

  @Column(nullable = false)
  private Achievements.Level level;


}
