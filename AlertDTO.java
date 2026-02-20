package com.example.smartwaste.dto;

import com.example.smartwaste.model.AlertSeverity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {
    private Long id;
    private String binId;
    private LocalDateTime timestamp;
    private String message;
    private AlertSeverity severity;
}
