package com.atri.tms.dto;

import com.atri.tms.entity.LoadStatus;
import com.atri.tms.entity.WeightUnit;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class LoadResponse {
    private UUID loadId;
    private String shipperId;
    private String loadingCity;
    private String unloadingCity;
    private Instant loadingDate;
    private String productType;
    private double weight;
    private WeightUnit weightUnit;
    private String truckType;
    private int noOfTrucks;
    private LoadStatus status;
    private Instant datePosted;
}
