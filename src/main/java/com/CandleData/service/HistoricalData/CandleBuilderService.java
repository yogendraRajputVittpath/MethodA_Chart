package com.CandleData.service.HistoricalData;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CandleBuilderService {

    public String getRoundedTimestamp(LocalDateTime time, String interval) {

        switch (interval) {
            case "minute":
                return time.truncatedTo(ChronoUnit.MINUTES).toString();

            case "5minute":
                int minute = (time.getMinute() / 5) * 5;
                return time.withMinute(minute).withSecond(0).withNano(0).toString();

            case "15minute":
                int min15 = (time.getMinute() / 15) * 15;
                return time.withMinute(min15).withSecond(0).withNano(0).toString();

            case "60minute":
                return time.withMinute(0).withSecond(0).withNano(0).toString();

            case "day":
                return time.toLocalDate().atStartOfDay().toString();

            case "week":
                return time.minusDays(time.getDayOfWeek().getValue() - 1)
                        .toLocalDate().atStartOfDay().toString();

            default:
                throw new IllegalArgumentException("Invalid interval");
        }
    }

    public Map<String, Object> updateCandle(Map<String, Object> existing, double price, long volume) {

        if (existing == null) {
            Map<String, Object> candle = new LinkedHashMap<>();
            candle.put("open_price", price);
            candle.put("high_price", price);
            candle.put("low_price", price);
            candle.put("close_price", price);
            candle.put("volume", volume);
            return candle;
        }

        existing.put("high_price", Math.max((double) existing.get("high_price"), price));
        existing.put("low_price", Math.min((double) existing.get("low_price"), price));
        existing.put("close_price", price);
        existing.put("volume", volume);

        return existing;
    }
}