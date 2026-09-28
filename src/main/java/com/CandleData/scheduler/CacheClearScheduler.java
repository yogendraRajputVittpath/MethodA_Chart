package com.CandleData.scheduler;

import com.CandleData.cache.HistoricalCache;
import com.CandleData.cache.LiveCandleCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheClearScheduler {

    private final HistoricalCache historicalCache;
    private final LiveCandleCache liveCandleCache;

    // 3:31 PM -> clear both caches
    @Scheduled(cron = "0 32 15 * * MON-FRI")
    public void clearAllCache() {
        log.info("Clearing ALL caches after market close...");

        historicalCache.clear();
        liveCandleCache.clear(); 

        log.info("Caches cleared successfully.");
    }
}