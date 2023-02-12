package fr.openobservatory.backend.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Account implements Comparable<Account> {

    @Id
    @GeneratedValue
    @Column
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private AccountType type;

    private String avatarUrl;

    private String biography;

    private Boolean publicProfil;

    private Date created_at;

    @Override
    public int compareTo(Account o) {
        return username.compareTo(o.username);
    }
}
