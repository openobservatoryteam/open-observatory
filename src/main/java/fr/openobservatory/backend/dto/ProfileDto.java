package fr.openobservatory.backend.dto;

import lombok.Data;

@Data
public class ProfileDto {
    private AchievementDto[] achievements;
    private String biography;
    private Integer karma;
    private UserDto user;
}
