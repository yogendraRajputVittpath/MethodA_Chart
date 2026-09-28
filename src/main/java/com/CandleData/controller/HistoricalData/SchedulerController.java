package com.CandleData.controller.HistoricalData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.CandleData.service.HistoricalData.SchedulerManualService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
@Slf4j
public class SchedulerController {

    private final SchedulerManualService schedulerService;

    //Historical Sync
    @PostMapping("/run-historical")
    public ResponseEntity<?> runHistorical() throws KiteException {
        log.info("API Hit: run-historical");

        String response = schedulerService.runHistoricalSync();
        return ResponseEntity.ok(response);
    }

    //Clear Cache
    @PostMapping("/clear-cache")
    public ResponseEntity<?> clearCache() {
        log.info("API Hit: clear-cache");

        String response = schedulerService.clearCache();
        return ResponseEntity.ok(response);
    }

    // Reload Cache
    @PostMapping("/reload-cache")
    public ResponseEntity<?> reloadCache() {
        log.info("API Hit: reload-cache");

        String response = schedulerService.reloadCache();
        return ResponseEntity.ok(response);
    }
}