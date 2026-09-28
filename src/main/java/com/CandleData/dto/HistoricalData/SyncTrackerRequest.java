package com.CandleData.dto.HistoricalData;

import lombok.Data;

@Data
public class SyncTrackerRequest {
    private String instrumentToken;
    private String interval;
    private String lastFetchedTimestamp;
    private String lastRunAt;
    
    private String status;
    private String tradingSymbol;
}