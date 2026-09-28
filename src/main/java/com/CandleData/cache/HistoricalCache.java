package com.CandleData.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class HistoricalCache {

    // interval → token → list of candles
    private final Map<String, Map<Long, List<Map<String, Object>>>> cache = new ConcurrentHashMap<>();

    public void put(String interval, Long token, List<Map<String, Object>> data) {
        cache.computeIfAbsent(interval, k -> new ConcurrentHashMap<>())
             .put(token, data);
    }

    public List<Map<String, Object>> get(String interval, Long token) {
        return cache.getOrDefault(interval, Collections.emptyMap())
                .getOrDefault(token, Collections.emptyList());
    }
    
    public void clear() {
        log.info("Clearing Historical Cache...");
        cache.clear();
    }
}