package com.CandleData.service.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.CandleData.entity.HistoricalData.SyncTracker;

@Service
public class AppUtils {

	 @Value("${historical.sync.range.minute}")
	    private int minuteRange;

	    @Value("${historical.sync.range.5minute}")
	    private int fiveMinuteRange;

	    @Value("${historical.sync.range.default}")
	    private int defaultRange;

	    private static final SimpleDateFormat FORMAT =
	            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
    public Date determineStartDate(String interval) {

        int days;

        switch (interval) {

            case "minute":
                days = minuteRange;
                break;

            case "5minute":
                days = fiveMinuteRange;
                break;

            default:
                days = defaultRange;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate baseDate;

        if (LocalTime.now().isBefore(LocalTime.of(15, 30))) {
            baseDate = today.minusDays(1);
        } else {
            baseDate = today;
        }

        LocalDate startDate = baseDate.minusDays(days);

        String start = startDate + " 09:15:00";

        try {
            return FORMAT.parse(start);
        } catch (Exception e) {
            throw new RuntimeException("Invalid start date", e);
        }
    }


    public Date determineToDate() {

        LocalDate today = LocalDate.now();
        LocalDate targetDate;
        LocalTime now = LocalTime.now();
        
        if (now.isBefore(LocalTime.of(15, 35))) {
            targetDate = today.minusDays(1);
        } else {
            targetDate = today;
        }

        String end = targetDate + " 15:30:00";

        try {
            return FORMAT.parse(end);
        } catch (Exception e) {
            throw new RuntimeException("Invalid end date", e);
        }
    }

    public boolean isAlreadySyncedToday(SyncTracker tracker) {

        return tracker.getLastRunAt() != null &&
                tracker.getLastRunAt().toLocalDate().equals(LocalDate.now()) &&
                "SUCCESS".equals(tracker.getStatus());
    }
    
    public Date getCutoffDate(String interval) {

        int days;

        switch (interval) {
            case "minute":
                days = 5;
                break;
            case "5minute":
                days = 15;
                break;
            default:
                days = 30;
        }

        LocalDate cutoff = LocalDate.now().minusDays(days - 1);
        String dateTime = cutoff + " 00:00:00";

        try {
            return FORMAT.parse(dateTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}