package com.example.smartwaste.service;

import com.example.smartwaste.dto.BinDTO;
import com.example.smartwaste.model.BinStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final BinService binService;

    /**
     * AI-Driven Route Optimization (Nearest Neighbor Algorithm)
     * 1. Filters bins that are FULL or NEAR_FULL.
     * 2. Starts from a base point (or the first critical bin).
     * 3. Greedily picks the next closest bin.
     */
    public List<BinDTO> getOptimizedRoute() {
        List<BinDTO> allBins = binService.getAllBins();
        
        // Filter bins that need collection
        List<BinDTO> criticalBins = allBins.stream()
                .filter(b -> b.getStatus() == BinStatus.FULL || b.getStatus() == BinStatus.NEAR_FULL)
                .collect(Collectors.toCollection(ArrayList::new));

        if (criticalBins.isEmpty()) {
            return Collections.emptyList();
        }

        List<BinDTO> optimizedRoute = new ArrayList<>();
        
        // Starting point (Default: Delhi Center or first bin)
        double currentLat = 28.6139;
        double currentLng = 77.2090;

        while (!criticalBins.isEmpty()) {
            BinDTO nearest = null;
            double minDistance = Double.MAX_VALUE;
            int nearestIndex = -1;

            for (int i = 0; i < criticalBins.size(); i++) {
                BinDTO bin = criticalBins.get(i);
                double dist = calculateDistance(currentLat, currentLng, bin.getLat(), bin.getLng());
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = bin;
                    nearestIndex = i;
                }
            }

            if (nearest != null) {
                optimizedRoute.add(nearest);
                currentLat = nearest.getLat();
                currentLng = nearest.getLng();
                criticalBins.remove(nearestIndex);
            }
        }

        return optimizedRoute;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
