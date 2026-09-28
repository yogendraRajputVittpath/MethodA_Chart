package com.CandleData.service.Impl.HistoricalData;

import com.CandleData.cache.HistoricalCache;
import com.CandleData.cache.LiveCandleCache;
import com.CandleData.dto.HistoricalData.HistoricalDataRequest;
import com.CandleData.repository.HistoricalData.HistoricalDataRepository;
import com.CandleData.service.HistoricalData.ReadHistoricalDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadHistoricalDataServiceImpl implements ReadHistoricalDataService {

    private final HistoricalCache historicalCache;
    private final LiveCandleCache liveCandleCache;
    private final HistoricalDataRepository historicalRepository;

    @Override
    public Map<String, Object> getHistoricalData(HistoricalDataRequest request) {

        String interval = request.getInterval().toLowerCase();
        Long token = request.getInstrumentToken();

        boolean isMarketOpen = isMarketOpen();

        List<Map<String, Object>> combined;

        if (isMarketOpen) {
        	log.info("Fetching Data from Cache In market hours.");
            // CACHE FLOW (MARKET HOURS)
            List<Map<String, Object>> historical =
                    historicalCache.get(interval, token);

            List<Map<String, Object>> live =
                    liveCandleCache.getLiveBuffer(token, interval);

            Map<String, Map<String, Object>> merged = new LinkedHashMap<>();

            historical.forEach(c ->
                    merged.put(c.get("time_stamp").toString(), c)
            );

            live.forEach(c ->
                    merged.put(c.get("time_stamp").toString(), c)
            );

            List<Map<String, Object>> allData = new ArrayList<>(merged.values());

            // APPLY FILTER HERE
            combined = filterByDateRange(
                    allData,
                    request.getFromDate(),
                    request.getToDate()
            );
        } else {
        	log.info("Fetching Data from DB after  market hours.");
            combined = fetchFromDB(
                    interval,
                    token,
                    request.getFromDate(),
                    request.getToDate()
            );
        }
        
        combined.sort(Comparator.comparing(m -> (Comparable) m.get("time_stamp")));
        
        if (combined == null || combined.isEmpty()) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", "NOT_FOUND");
            res.put("message", "No data found for given date range");
            res.put("data", Collections.emptyList());
            return res;
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "SUCCESS");
        res.put("data", combined);
        return res;
    }
    
    private List<Map<String, Object>> filterByDateRange(
            List<Map<String, Object>> data,
            Date from,
            Date to
    ) {
        if (from == null || to == null) return data;

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : data) {
            try {
                String ts = row.get("time_stamp").toString();

                ts = ts.replace("T", " ");
                if (ts.length() == 16) {
                    ts += ":00";
                }

                Date rowDate = java.sql.Timestamp.valueOf(ts);

                if (!rowDate.before(from) && !rowDate.after(to)) {
                    result.add(row);
                }

            } catch (Exception e) {
            	log.info("Invalid timestamp AFTER FIX: " + row.get("time_stamp"));
            }
        }

        log.info("FINAL FILTER SIZE: " + result.size());
        return result;
    }
    
    private List<Map<String, Object>> fetchFromDB(String interval, Long token, Date from, Date to) {

        List<Object[]> rows = historicalRepository.fetchLatest(interval, token, from, to);
        //log.info("data Received from DB : {}",rows);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("time_stamp", row[0].toString());
            map.put("open_price", row[1]);
            map.put("high_price", row[2]);
            map.put("low_price", row[3]);
            map.put("close_price", row[4]);
            map.put("volume", row[5]);
            result.add(map);
        }
        
        //log.info("Data AFter mapping : {}",result);
        
        return result;
    }
    
    private boolean isMarketOpen() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (now.getDayOfWeek().getValue() >= 6) return false;

        int minutes = now.getHour() * 60 + now.getMinute();
        return minutes >= (9 * 60 + 15) && minutes <= (15 * 60 + 30);
    }
}