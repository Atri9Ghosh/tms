package com.atri.tms.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TransporterResponse {
    private UUID transporterId;
    private String companyName;
    private double rating;
    private List<TransporterTruckDto> availableTrucks;
}
