package com.CandleData.service.HistoricalData;

import com.CandleData.cache.HistoricalCache;
import com.CandleData.config.kite.AppConfigProperties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPreloadService {

    @PersistenceContext
    private EntityManager entityManager;

    private final HistoricalCache historicalCache;
    private final AppConfigProperties config;
    private volatile List<Long> activeTokens = new ArrayList<>();
    private static final List<String> INTERVALS = List.of(
            "minute", "5minute", "15minute", "60minute", "day", "week"
    );

    //private static final int LIMIT = 500;

    public List<Long> getActiveTokens() {
        return this.activeTokens;
    }

    // NON-BLOCKING STARTUP
    @EventListener(ApplicationReadyEvent.class)
    public void loadOnStartup() {
        log.info("Initial Historical Data Load Started (Async)...");
//        new Thread(this::loadData).start();
        CompletableFuture.runAsync(this::loadData);
    }

    @Scheduled(cron = "0 45 8 * * ?")
    public void refreshDaily() {
        log.info("Daily Cache Refresh Started...");
        historicalCache.clear();
//        loadData();
        CompletableFuture.runAsync(this::loadData);
    }

    @Async("taskExecutor")
    public void loadData() {
        try {
        	 LocalDate start = LocalDate.now().minusDays(5);
             String startDateTime = start + " 09:15:00";

             LocalDate end = LocalDate.now();
             String endDateTime = end + " 15:30:00";

            // Get tokens
            List<Number> tokenResults = entityManager.createNativeQuery(
                    "SELECT DISTINCT instrument_token FROM week_historicaldata_eq"
            ).getResultList();

            this.activeTokens = tokenResults.stream()
                    .map(Number::longValue)
                    .collect(Collectors.toList());

            log.info("Total Tokens Found in DB: {}", activeTokens.size());

            if (activeTokens.isEmpty()) {
                log.warn("No tokens found to load in cache.");
                return;
            }

            // PARALLEL LOAD (FAST)
//            activeTokens.parallelStream().forEach(token -> {
//                for (String interval : INTERVALS) {
//                    loadTokenIntervalData(token, interval, startDateTime, endDateTime);
//                }
//            });
//
//            log.info("Historical Cache Loaded Successfully");
//
//        } catch (Exception e) {
//            log.error("Critical error during data preload", e);
//        }
            for (Long token : activeTokens) {
                for (String interval : INTERVALS) {
                    loadTokenIntervalData(token, interval, startDateTime, endDateTime);
                }
            }
            log.info("Historical Cache Loaded Successfully");
        } catch (Exception e) {
            log.error("Critical error during data preload", e);
        }
    }
    

    private void loadTokenIntervalData(Long token, String interval, String startDateTime, String endDateTime) {
        try {
            String table = interval + "_historicaldata_eq";

            String sql = "SELECT time_stamp, open_price, high_price, low_price, close_price, volume " +
                    "FROM " + table +
                    " WHERE instrument_token = :token " +
                    " AND time_stamp BETWEEN :startDateTime AND :endDateTime " +
                    " ORDER BY time_stamp ASC";

            List<Object[]> rows = entityManager.createNativeQuery(sql)
                    .setParameter("token", token)
                    .setParameter("startDateTime", startDateTime)
                    .setParameter("endDateTime", endDateTime)
                    .getResultList();

            if (rows.isEmpty()) return;

            List<Map<String, Object>> data = new ArrayList<>();

            for (Object[] row : rows) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("time_stamp", row[0].toString());
                map.put("open_price", row[1]);
                map.put("high_price", row[2]);
                map.put("low_price", row[3]);
                map.put("close_price", row[4]);
                map.put("volume", row[5]);
                data.add(map);
            }

            historicalCache.put(interval, token, data);

        } catch (Exception e) {
            log.error("Error loading token {} for interval {}: {}", token, interval, e.getMessage());
        }
    }
 }