package com.CandleData.controller.HistoricalData;


import com.CandleData.dto.HistoricalData.SyncTrackerRequest;
import com.CandleData.entity.HistoricalData.SyncTracker;
import com.CandleData.service.HistoricalData.SyncTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/get/sync-tracker")
@RequiredArgsConstructor
@Slf4j
public class SyncTrackerController {

    private final SyncTrackerService service;

    @GetMapping("/data")
    public ResponseEntity<?> getData(@ModelAttribute SyncTrackerRequest request) {

        try {
            List<SyncTracker> data = service.getAllSyncTrackerData(request);
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error("Error fetching tracker data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}