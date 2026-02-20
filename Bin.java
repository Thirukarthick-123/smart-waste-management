package com.example.smartwaste.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity that maps to the *bins* table.
 * The table has columns: bin_id, fill, lat, lng, status, last_update
 */
@Entity
@Table(name = "bins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bin_id", nullable = false, unique = true, length = 50)
    private String binId;

    @Column(nullable = false)
    private Integer fill;

    // <-- explicit column names – they match the table exactly
    @Column(name = "lat", nullable = false)
    private Double lat; // latitude (‑90 … +90)

    @Column(name = "lng", nullable = false)
    private Double lng; // longitude (‑180 … +180)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BinStatus status;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}
