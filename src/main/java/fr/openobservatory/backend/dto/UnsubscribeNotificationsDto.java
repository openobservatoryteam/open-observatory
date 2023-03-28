package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnsubscribeNotificationsDto {

    @NotBlank  private String endpoint;
}
