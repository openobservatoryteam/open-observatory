package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Table(name = "user_achievement")
public class UserAchievementEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "achievement_id", nullable = false)
  private AchievementEntity achievement;

  @Column(nullable = false)
  private Level level;

  @AllArgsConstructor
  @Getter
  public enum Level {
    NEW(1),
    BEGINNER(10),
    INTERMEDIATES(25),
    EXPERT(100);

    private final int count;

    public static Level getLevel(Integer count) {
      if (count == 1) {
        return Level.NEW;
      } else if (count == 10) {
        return Level.BEGINNER;
      } else if (count == 25) {
        return Level.INTERMEDIATES;
      } else {
        return EXPERT;
      }
    }
  }
}
