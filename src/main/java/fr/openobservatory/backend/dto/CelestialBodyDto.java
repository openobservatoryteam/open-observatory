package fr.openobservatory.backend.dto;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class CelestialBodyDto {

    @Setter(AccessLevel.NONE)
    private UUID id;
    private String name;
    private String image;
    private Integer validityTime;

}
