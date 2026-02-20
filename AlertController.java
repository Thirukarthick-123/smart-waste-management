    package com.example.smartwaste.controller;

    import com.example.smartwaste.dto.AlertDTO;
    import com.example.smartwaste.model.Alert;
    import com.example.smartwaste.service.AlertService;
    import lombok.RequiredArgsConstructor;
    import org.modelmapper.ModelMapper;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/api/alerts")
    @RequiredArgsConstructor
    public class AlertController {

        private final AlertService alertService;
        private final ModelMapper modelMapper = new ModelMapper();

        /** Public – list all alerts */
        @GetMapping
        public List<AlertDTO> getAll() {
            return alertService.getAllAlerts().stream()
                    .map(a -> AlertDTO.builder()
                            .id(a.getId())
                            .binId(a.getBin().getBinId())
                            .timestamp(a.getTimestamp())
                            .message(a.getMessage())
                            .severity(a.getSeverity())
                            .build())
                    .collect(Collectors.toList());
        }

        /** Admin only – delete an alert (optional) */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
            try {
                alertService.deleteAlert(id);
                return ResponseEntity.noContent().build();
            } catch (java.util.NoSuchElementException ex) {
                return ResponseEntity.notFound().build();
            }
        }
    }
