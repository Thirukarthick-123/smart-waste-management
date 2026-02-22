package com.example.smartwaste.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt‑encoded password */
    @Column(nullable = false)
    private String password;

    /** Spring Security role (e.g. ROLE_ADMIN) */
    @Column(nullable = false, length = 20)
    private String role;
}
