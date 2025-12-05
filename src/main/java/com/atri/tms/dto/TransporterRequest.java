package com.atri.tms.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransporterRequest {
    private String companyName;
    private double rating;
    private List<TransporterTruckDto> availableTrucks;
}
