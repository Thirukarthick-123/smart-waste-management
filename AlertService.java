package com.example.smartwaste.service;

import com.example.smartwaste.model.*;
import com.example.smartwaste.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    /**
     * Called whenever a Bin is updated (by the Arduino listener or an API call).
     * Generates an Alert if the fill level crosses defined thresholds.
     */
    public void evaluateAndCreateAlert(Bin bin) {
        // Determine if an alert is required
        AlertSeverity severity = null;
        String message = null;

        if (bin.getStatus() == BinStatus.OVERFLOW) {
            severity = AlertSeverity.CRITICAL;
            message = "Bin overflow – fill 100%";
        } else if (bin.getStatus() == BinStatus.NEAR_FULL) {
            severity = AlertSeverity.WARN;
            message = "Bin near full (≥80%)";
        } else if (bin.getFill() == 0) {
            // Example: you may want an alert when bin is empty – optional
            severity = AlertSeverity.INFO;
            message = "Bin empty";
        }

        if (severity != null) {
            Alert alert = Alert.builder()
                    .bin(bin)
                    .timestamp(LocalDateTime.now())
                    .severity(severity)
                    .message(message)
                    .build();
            alertRepository.save(alert);
        }
    }

    public java.util.List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new java.util.NoSuchElementException("Alert not found");
        }
        alertRepository.deleteById(id);
    }
}
