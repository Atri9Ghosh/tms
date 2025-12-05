package com.atri.tms.controller;

import com.atri.tms.dto.TransporterRequest;
import com.atri.tms.dto.TransporterResponse;
import com.atri.tms.dto.TransporterTruckDto;
import com.atri.tms.service.TransporterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transporter")
@RequiredArgsConstructor
public class TransporterController {

    private final TransporterService transporterService;

    // POST /transporter
    @PostMapping
    public ResponseEntity<TransporterResponse> createTransporter(
            @RequestBody TransporterRequest request) {
        TransporterResponse response = transporterService.createTransporter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /transporter/{id}
    @GetMapping("/{transporterId}")
    public TransporterResponse getTransporter(@PathVariable UUID transporterId) {
        return transporterService.getTransporter(transporterId);
    }

    // PUT /transporter/{id}/trucks
    @PutMapping("/{transporterId}/trucks")
    public TransporterResponse updateTrucks(
            @PathVariable UUID transporterId,
            @RequestBody List<TransporterTruckDto> trucks) {
        return transporterService.updateTrucks(transporterId, trucks);
    }
}
