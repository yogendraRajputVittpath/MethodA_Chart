package com.CandleData.service.HistoricalData;

import com.CandleData.scheduler.CacheClearScheduler;
import com.CandleData.scheduler.HistoricalDataScheduler;
import com.CandleData.service.HistoricalData.DataPreloadService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerManualService {

    private final HistoricalDataScheduler historicalScheduler;
    private final CacheClearScheduler cacheClearScheduler;
    private final DataPreloadService dataPreloadService;

    //Run Historical Sync
    public String runHistoricalSync() throws KiteException {
        try {
            log.info("Manual Trigger: Historical Sync Started");

            historicalScheduler.runDailySync();

            log.info("Manual Trigger: Historical Sync Completed");
            return "Historical Sync executed successfully";

        } catch (Exception e) {
            log.error("Manual Historical Sync Failed", e);
            return "Historical Sync Failed: " + e.getMessage();
        }
    }

    //Clear Cache
    public String clearCache() {
        try {
            log.info("Manual Trigger: Cache Clear Started");

            cacheClearScheduler.clearAllCache();

            log.info("Manual Trigger: Cache Cleared");
            return "Cache cleared successfully";

        } catch (Exception e) {
            log.error("Manual Cache Clear Failed", e);
            return "Cache Clear Failed: " + e.getMessage();
        }
    }

    //Reload Cache Data
    public String reloadCache() {
        try {
            log.info("Manual Trigger: Data Preload Started");

//            dataPreloadService.loadData();
            CompletableFuture.runAsync(() -> {
                dataPreloadService.loadData();
            });

            log.info("Manual Trigger: Data Preload Completed");
            return "Data Preloaded successfully";

        } catch (Exception e) {
            log.error("Manual Data Preload Failed", e);
            return "Data Preload Failed: " + e.getMessage();
        }
    }
}