package com.humanin.planpaz.controller;

import com.humanin.planpaz.dto.LocationResponseDTO;
import com.humanin.planpaz.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localizacoes")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationResponseDTO>> buscar(
            @RequestParam String busca
    ) {
        return ResponseEntity.ok(locationService.buscar(busca));
    }
}