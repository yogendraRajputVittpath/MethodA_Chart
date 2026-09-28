package com.CandleData.service.HistoricalData;


import com.CandleData.dto.HistoricalData.SyncTrackerRequest;
import com.CandleData.entity.HistoricalData.SyncTracker;

import java.util.List;

public interface SyncTrackerService {
    List<SyncTracker> getAllSyncTrackerData(SyncTrackerRequest request);
}