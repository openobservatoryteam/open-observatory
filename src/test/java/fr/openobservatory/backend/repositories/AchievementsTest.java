package fr.openobservatory.backend.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AchievementsTest {

    @DisplayName("Achievements.Level#getLevel should return corresponding level with valid input")
    @Test
    public void achievementsLevel_getLevel_should_return_corresponding_level_with_valid_input() {
        //Given
        var count = 25;
        var expected = Achievements.Level.INTERMEDIATES;
        //When
        var level =  Achievements.Level.getLevel(count);
        //Then
        assertThat(level).isEqualTo(expected);
    }

    @DisplayName("Achievements.Level#getLevel should return null with invalid input")
    @Test
    public void achievementsLevel_getLevel_should_return_null_with_invalid_input() {
        //Given
        var count = 5;
        //When
        var level =  Achievements.Level.getLevel(count);
        //Then
        assertThat(level).isNull();
    }

}