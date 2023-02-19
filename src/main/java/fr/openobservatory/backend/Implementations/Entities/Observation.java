package fr.openobservatory.backend.Implementations.Entities;

import fr.openobservatory.backend.Data.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity @Data @AllArgsConstructor @NoArgsConstructor
public class Observation
{
    //entity id
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    //observation description
    private String description;
    //observation latitude
    private double latitude;
    //observation longitude
    private double longitude;
    //observation orientation
    private Integer orientation;
    //celestial body observerd
    private Integer celestial_body_id;
    //visibility level of the observation
    private Visibility visibility;
    //author of the observation
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    //creation date of the observation
    private Timestamp created_at;
}
