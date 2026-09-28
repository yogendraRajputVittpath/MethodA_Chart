package com.CandleData.cache;

import com.CandleData.service.HistoricalData.CandleBuilderService;
import com.zerodhatech.models.Tick;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class LiveCandleCache {

    private final CandleBuilderService candleBuilderService;

    private final Map<String, Map<Long, LinkedHashMap<String, Map<String, Object>>>> intervalCache = new ConcurrentHashMap<>();

    private static final int MAX_CANDLES = 1106;

    private static final List<String> INTERVALS = List.of(
            "minute", "5minute", "15minute", "60minute", "day", "week"
    );

    public void updateTick(Tick tick) {

        if (tick == null) {
            log.warn("Received null tick");
            return;
        }

        try {
            Long token = tick.getInstrumentToken();
            double price = tick.getLastTradedPrice();
            long volume = tick.getVolumeTradedToday();
            LocalDateTime now = LocalDateTime.now();
            
            // MARKET HOURS FILTER
            if (!isMarketOpen(now)) {
                //log.debug("Market closed -> skipping tick for token {}", token);
                return;
            }

            // LIVE DATA 
            //log.debug("LIVE TICK -> Token: {}, Price: {}, Volume: {}", token, price, volume);

            for (String interval : INTERVALS) {

                try {
                    String roundedTime = candleBuilderService.getRoundedTimestamp(now, interval);

                    intervalCache.computeIfAbsent(interval, k -> {
                        log.debug("Creating interval cache for {}", interval);
                        return new ConcurrentHashMap<>();
                    });

                    Map<Long, LinkedHashMap<String, Map<String, Object>>> tokenMap = intervalCache.get(interval);

                    tokenMap.computeIfAbsent(token, k -> {
                        log.debug("Creating token cache for token {} in interval {}", token, interval);
                        return new LinkedHashMap<>();
                    });

                    LinkedHashMap<String, Map<String, Object>> candleMap = tokenMap.get(token);

                    synchronized (candleMap) {

                        Map<String, Object> existing = candleMap.get(roundedTime);

                        Map<String, Object> updated = candleBuilderService.updateCandle(existing, price, volume);

                        updated.put("time_stamp", roundedTime);

                        candleMap.put(roundedTime, updated);

                        // Candle Update Log
                        //log.debug("CANDLE UPDATED -> Token: {}, Interval: {}, Time: {}", token, interval, roundedTime);

                        // limit size
                        if (candleMap.size() > MAX_CANDLES) {
                            Iterator<String> it = candleMap.keySet().iterator();
                            if (it.hasNext()) {
                                String removedKey = it.next();
                                it.remove();
                                log.debug("Removed oldest candle -> Token: {}, Interval: {}, Time: {}", token, interval, removedKey);
                            }
                        }
                    }

                } catch (Exception intervalEx) {
                    log.error("Error processing interval {} for token {}", interval, token, intervalEx);
                }
            }

        } catch (Exception e) {
            log.error("Error updating tick globally", e);
        }
    }

    private boolean isMarketOpen(LocalDateTime time) {

        if (time.getDayOfWeek() == DayOfWeek.SATURDAY ||
            time.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }

        int hour = time.getHour();
        int minute = time.getMinute();

        int currentMinutes = hour * 60 + minute;

        int marketStart = 9 * 60 + 15;   // 9:15 AM
        int marketEnd = 15 * 60 + 30;    // 3:30 PM

        return currentMinutes >= marketStart && currentMinutes <= marketEnd;
    }


    public List<Map<String, Object>> getLiveBuffer(Long token, String interval) {

        try {
            if (token == null || interval == null) {
                log.warn("Invalid input -> token: {}, interval: {}", token, interval);
                return Collections.emptyList();
            }

            if (!intervalCache.containsKey(interval)) {
                log.debug("Interval {} not found in cache", interval);
                return Collections.emptyList();
            }

            Map<Long, LinkedHashMap<String, Map<String, Object>>> tokenMap = intervalCache.get(interval);

            if (!tokenMap.containsKey(token)) {
                log.debug("Token {} not found in interval {}", token, interval);
                return Collections.emptyList();
            }

            List<Map<String, Object>> result = new ArrayList<>(tokenMap.get(token).values());

            log.info("Fetched live buffer -> Token: {}, Interval: {}, Size: {}", token, interval, result.size());

            return result;

        } catch (Exception e) {
            log.error("Error fetching live buffer for token {} interval {}", token, interval, e);
            return Collections.emptyList();
        }
    }

    public void clear() {
        log.info("Clearing Live Candle Cache...");
        intervalCache.clear();
    }
}