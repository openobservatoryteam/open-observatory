package fr.openobservatory.backend.Implementations.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data @AllArgsConstructor
public class ObservationDto
{
    private Integer id;
    private String description;
    private double latitude;
    private double longitude;
    private Integer orientation;
    private OffsetDateTime time;
    private UserDto author;
}
