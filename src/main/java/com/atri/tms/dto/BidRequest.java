package com.atri.tms.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BidRequest {
    private UUID loadId;
    private UUID transporterId;
    private double proposedRate;
    private int trucksOffered;
}
