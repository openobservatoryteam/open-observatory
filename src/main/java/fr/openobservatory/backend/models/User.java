package fr.openobservatory.backend.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class User implements Comparable<User> {

    @Id
    @GeneratedValue
    @Column
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private UserType type;

    private String avatarUrl;

    private String biography;

    private Boolean publicProfil;

    private Date created_at;

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }
}
