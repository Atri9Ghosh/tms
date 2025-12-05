package com.atri.tms.controller;

import com.atri.tms.dto.BidRequest;
import com.atri.tms.entity.Bid;
import com.atri.tms.entity.BidStatus;
import com.atri.tms.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    // POST /bid
    @PostMapping
    public ResponseEntity<Bid> createBid(@RequestBody BidRequest request) {
        Bid bid = bidService.createBid(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bid);
    }

    // GET /bid?loadId=&transporterId=&status=
    @GetMapping
    public List<Bid> filterBids(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) UUID transporterId,
            @RequestParam(required = false) BidStatus status
    ) {
        return bidService.filterBids(loadId, transporterId, status);
    }

    // GET /bid/{bidId}
    @GetMapping("/{bidId}")
    public Bid getBid(@PathVariable UUID bidId) {
        return bidService.getBid(bidId);
    }

    // PATCH /bid/{bidId}/reject
    @PatchMapping("/{bidId}/reject")
    public Bid rejectBid(@PathVariable UUID bidId) {
        return bidService.rejectBid(bidId);
    }
}
