package fr.openobservatory.backend.Implementations.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Entity @Table(name = "'User'") @Data @AllArgsConstructor @NoArgsConstructor
public class User
{
    //entity id
    @OneToMany(mappedBy = "author")
    private Collection<Observation> observations = new ArrayList<>();
}
