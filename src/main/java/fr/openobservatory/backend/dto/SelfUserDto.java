package fr.openobservatory.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SelfUserDto extends UserWithProfileDto {
    private Integer radius;
    private boolean notificationsEnabled;
}
