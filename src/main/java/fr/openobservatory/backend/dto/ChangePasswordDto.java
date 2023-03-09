package fr.openobservatory.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ChangePasswordDto {

    @JsonIgnore
    private String oldPassword;
    @JsonIgnore
    private String newPassword;
}
