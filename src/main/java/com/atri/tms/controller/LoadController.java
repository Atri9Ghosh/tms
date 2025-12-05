package com.atri.tms.controller;

import com.atri.tms.dto.LoadRequest;
import com.atri.tms.dto.LoadResponse;
import com.atri.tms.entity.Bid;
import com.atri.tms.entity.LoadStatus;
import com.atri.tms.service.LoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/load")
@RequiredArgsConstructor
public class LoadController {

    private final LoadService loadService;

    // POST /load → create load (status = POSTED)
    @PostMapping
    public ResponseEntity<LoadResponse> createLoad(@RequestBody LoadRequest request) {
        LoadResponse response = loadService.createLoad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /load?shipperId=&status=&page=&size=
    @GetMapping
    public Page<LoadResponse> listLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) LoadStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return loadService.listLoads(shipperId, status, page, size);
    }

    // GET /load/{loadId}
    @GetMapping("/{loadId}")
    public LoadResponse getLoad(@PathVariable UUID loadId) {
        return loadService.getLoad(loadId);
    }

    // PATCH /load/{loadId}/cancel
    @PatchMapping("/{loadId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelLoad(@PathVariable UUID loadId) {
        loadService.cancelLoad(loadId);
    }

    // GET /load/{loadId}/best-bids
    @GetMapping("/{loadId}/best-bids")
    public List<Bid> getBestBids(@PathVariable UUID loadId) {
        return loadService.getBestBids(loadId);
    }
}
