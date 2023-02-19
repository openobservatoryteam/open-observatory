package fr.openobservatory.backend.dto;

import lombok.Data;

@Data
public class CelestialBodyDto {

  private Long id;
  private String name;
  private String image;
  private Integer validityTime;
}
