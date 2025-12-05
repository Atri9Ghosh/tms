package com.atri.tms.service;

import com.atri.tms.dto.TransporterRequest;
import com.atri.tms.dto.TransporterResponse;
import com.atri.tms.dto.TransporterTruckDto;
import com.atri.tms.entity.Transporter;
import com.atri.tms.entity.TransporterTruck;
import com.atri.tms.exception.ResourceNotFoundException;
import com.atri.tms.repository.TransporterRepository;
import com.atri.tms.repository.TransporterTruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransporterService {

    private final TransporterRepository transporterRepository;
    private final TransporterTruckRepository transporterTruckRepository;

    // POST /transporter
    @Transactional
    public TransporterResponse createTransporter(TransporterRequest req) {

        Transporter transporter = Transporter.builder()
                .companyName(req.getCompanyName())
                .rating(req.getRating())
                .build();

        if (req.getAvailableTrucks() != null) {
            for (TransporterTruckDto t : req.getAvailableTrucks()) {
                TransporterTruck truck = TransporterTruck.builder()
                        .truckType(t.getTruckType())
                        .count(t.getCount())
                        .transporter(transporter)
                        .build();
                transporter.getAvailableTrucks().add(truck);
            }
        }

        Transporter saved = transporterRepository.save(transporter);
        return toResponse(saved);
    }

    // GET /transporter/{id}
    public TransporterResponse getTransporter(UUID transporterId) {
        Transporter t = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));
        return toResponse(t);
    }

    // PUT /transporter/{id}/trucks
    @Transactional
    public TransporterResponse updateTrucks(UUID transporterId, List<TransporterTruckDto> trucks) {
        Transporter transporter = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

        // simple approach: clear and recreate
        transporter.getAvailableTrucks().clear();

        for (TransporterTruckDto dto : trucks) {
            TransporterTruck truck = TransporterTruck.builder()
                    .truckType(dto.getTruckType())
                    .count(dto.getCount())
                    .transporter(transporter)
                    .build();
            transporter.getAvailableTrucks().add(truck);
        }

        Transporter saved = transporterRepository.save(transporter);
        return toResponse(saved);
    }

    private TransporterResponse toResponse(Transporter t) {
        List<TransporterTruckDto> trucks = t.getAvailableTrucks().stream()
                .map(truck -> {
                    TransporterTruckDto dto = new TransporterTruckDto();
                    dto.setTruckType(truck.getTruckType());
                    dto.setCount(truck.getCount());
                    return dto;
                })
                .collect(Collectors.toList());

        return TransporterResponse.builder()
                .transporterId(t.getTransporterId())
                .companyName(t.getCompanyName())
                .rating(t.getRating())
                .availableTrucks(trucks)
                .build();
    }
}
