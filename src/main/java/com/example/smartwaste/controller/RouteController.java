package com.example.smartwaste.controller;

import com.example.smartwaste.dto.BinDTO;
import com.example.smartwaste.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/optimized")
    public List<BinDTO> getOptimizedRoute() {
        return routeService.getOptimizedRoute();
    }
}
