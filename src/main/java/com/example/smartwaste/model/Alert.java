package com.example.smartwaste.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The bin that triggered the alert */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", referencedColumnName = "id")
    private Bin bin;

    /** When the alert was generated */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** Human‑readable message */
    @Column(nullable = false, length = 255)
    private String message;

    /** Severity of the alert */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 20)
    private AlertSeverity severity;
}
