package com.example.smartwaste.controller;

import com.example.smartwaste.dto.BinDTO;
import com.example.smartwaste.model.BinStatus;
import com.example.smartwaste.service.BinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/bins")
@RequiredArgsConstructor
public class BinController {

    private final BinService binService;

    /** Public – list all bins */
    @GetMapping
    public List<BinDTO> getAll() {
        return binService.getAllBins();
    }

    /** Public – get a single bin by DB id */
    @GetMapping("/{id}")
    public ResponseEntity<BinDTO> getById(@PathVariable Long id) {
        return binService.getBinById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Public – filter / search */
    @GetMapping("/search")
    public List<BinDTO> search(
            @RequestParam Optional<BinStatus> status,
            @RequestParam Optional<Integer> minFill,
            @RequestParam Optional<Integer> maxFill,
            @RequestParam Optional<Double> lat,
            @RequestParam Optional<Double> lng,
            @RequestParam Optional<Double> radiusKm) {

        return binService.filter(status, minFill, maxFill, lat, lng, radiusKm);
    }

    /** Admin only – create a new bin (rarely needed because Arduino creates it) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BinDTO> create(@Valid @RequestBody BinDTO dto) {
        BinDTO created = binService.createBin(dto);
        return ResponseEntity.ok(created);
    }

    /** Admin only – update an existing bin (by DB id) */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BinDTO> update(@PathVariable Long id,
            @Valid @RequestBody BinDTO dto) {
        try {
            BinDTO updated = binService.updateBin(id, dto);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Admin only – delete a bin */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            binService.deleteBin(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Telemetry – receive data from hardware (ESP32/Wokwi) */
    @PostMapping("/telemetry")
    public ResponseEntity<Void> telemetry(@RequestBody BinDTO dto) {
        binService.processIncomingData(dto);
        return ResponseEntity.ok().build();
    }
}
