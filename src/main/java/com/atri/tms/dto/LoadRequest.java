package com.atri.tms.dto;

import com.atri.tms.entity.WeightUnit;
import lombok.Data;

import java.time.Instant;

@Data
public class LoadRequest {
    private String shipperId;
    private String loadingCity;
    private String unloadingCity;
    private Instant loadingDate;
    private String productType;
    private double weight;
    private WeightUnit weightUnit;
    private String truckType;
    private int noOfTrucks;
}
