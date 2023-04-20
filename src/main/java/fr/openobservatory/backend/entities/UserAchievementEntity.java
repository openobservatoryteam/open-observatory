package fr.openobservatory.backend.entities;

import fr.openobservatory.backend.repositories.Achievements.Achievement;
import fr.openobservatory.backend.repositories.Achievements.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user_achievement")
public class UserAchievementEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Achievement achievement;

  @Column(nullable = false)
  private Level level;

  @ManyToOne(optional = false)
  private UserEntity user;
}
