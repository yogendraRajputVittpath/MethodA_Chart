package com.CandleData.service.HistoricalData;

import java.util.Map;
import com.CandleData.dto.HistoricalData.HistoricalDataRequest;

public interface ReadHistoricalDataService {
    Map<String, Object> getHistoricalData(HistoricalDataRequest request);
}