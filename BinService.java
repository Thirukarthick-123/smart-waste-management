package com.example.smartwaste.service;

import com.example.smartwaste.dto.BinDTO;
import com.example.smartwaste.model.*;
import com.example.smartwaste.repository.BinRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for {@link Bin} entities.
 * Includes extra {@code System.out.println} statements that make it easy to see
 * when data is really being persisted (useful while debugging the serial‑port
 * flow).
 */
@Service
@RequiredArgsConstructor
public class BinService {

    private final BinRepository binRepository;
    private final AlertService alertService;
    private final ModelMapper modelMapper = new ModelMapper();

    /* ------------------------------------------------------------------ */
    /* PUBLIC read‑only operations */
    /* ------------------------------------------------------------------ */
    public List<BinDTO> getAllBins() {
        long count = binRepository.count();
        System.out.println("🔍 getAllBins() – repo count = " + count);
        return binRepository.findAll()
                .stream()
                .map(b -> modelMapper.map(b, BinDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<BinDTO> getBinById(Long id) {
        return binRepository.findById(id)
                .map(b -> modelMapper.map(b, BinDTO.class));
    }

    public Optional<BinDTO> getBinByBinId(String binId) {
        return binRepository.findByBinId(binId)
                .map(b -> modelMapper.map(b, BinDTO.class));
    }

    /* ------------------------------------------------------------------ */
    /* CREATE / UPDATE / DELETE (transactional) */
    /* ------------------------------------------------------------------ */
    @Transactional
    public BinDTO createBin(BinDTO dto) {
        binRepository.findByBinId(dto.getBinId())
                .ifPresent(b -> {
                    throw new IllegalArgumentException("Bin with binId already exists");
                });

        Bin bin = modelMapper.map(dto, Bin.class);
        bin.setLastUpdate(LocalDateTime.now());

        Bin saved = binRepository.save(bin);
        System.out.println("💾 Bin created – id=" + saved.getId() + ", binId=" + saved.getBinId());

        return modelMapper.map(saved, BinDTO.class);
    }

    @Transactional
    public BinDTO updateBin(Long id, BinDTO dto) {
        Bin existing = binRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bin not found"));

        existing.setFill(dto.getFill());
        existing.setLat(dto.getLat());
        existing.setLng(dto.getLng());
        existing.setStatus(dto.getStatus());
        existing.setLastUpdate(LocalDateTime.now());

        Bin saved = binRepository.save(existing);
        System.out.println("🔄 Bin updated – id=" + saved.getId() + ", binId=" + saved.getBinId());

        return modelMapper.map(saved, BinDTO.class);
    }

    @Transactional
    public void deleteBin(Long id) {
        if (!binRepository.existsById(id)) {
            throw new NoSuchElementException("Bin not found");
        }
        binRepository.deleteById(id);
        System.out.println("🗑️ Bin deleted – id=" + id);
    }

    /* ------------------------------------------------------------------ */
    /** Called by the serial‑port listener for every JSON line that arrives */
    /* ------------------------------------------------------------------ */
    @Transactional
    public void processIncomingData(BinDTO incoming) {
        Bin bin = binRepository.findByBinId(incoming.getBinId())
                .orElseGet(() -> {
                    Bin fresh = new Bin();
                    fresh.setBinId(incoming.getBinId());
                    return fresh;
                });

        bin.setFill(incoming.getFill());
        bin.setLat(incoming.getLat());
        bin.setLng(incoming.getLng());
        bin.setStatus(incoming.getStatus());
        bin.setLastUpdate(LocalDateTime.now());

        Bin saved = binRepository.save(bin);
        System.out.println("📥 Processed incoming data – id=" + saved.getId()
                + ", binId=" + saved.getBinId()
                + ", fill=" + saved.getFill()
                + ", status=" + saved.getStatus());

        // Generate alerts if thresholds are crossed
        alertService.evaluateAndCreateAlert(saved);
    }

    /* ------------------------------------------------------------------ */
    /** Filter / search – all parameters optional */
    /* ------------------------------------------------------------------ */
    public List<BinDTO> filter(Optional<BinStatus> status,
            Optional<Integer> minFill,
            Optional<Integer> maxFill,
            Optional<Double> lat,
            Optional<Double> lng,
            Optional<Double> radiusKm) {

        return binRepository.findAll().stream()
                .filter(b -> status.map(s -> b.getStatus() == s).orElse(true))
                .filter(b -> minFill.map(min -> b.getFill() >= min).orElse(true))
                .filter(b -> maxFill.map(max -> b.getFill() <= max).orElse(true))
                .filter(b -> {
                    if (lat.isPresent() && lng.isPresent() && radiusKm.isPresent()) {
                        double distance = haversine(lat.get(), lng.get(),
                                b.getLat(), b.getLng());
                        return distance <= radiusKm.get();
                    }
                    return true;
                })
                .map(b -> modelMapper.map(b, BinDTO.class))
                .collect(Collectors.toList());
    }

    /* ------------------------------------------------------------------ */
    /** Haversine – distance in kilometres between two lat/lng points */
    /* ------------------------------------------------------------------ */
    private double haversine(double lat1, double lon1,
            double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
