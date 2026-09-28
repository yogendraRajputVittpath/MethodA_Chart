package com.CandleData.service.kite;

import com.CandleData.cache.LiveCandleCache;
import com.zerodhatech.ticker.KiteTicker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KiteTickerService {

    private final KiteService kiteService;
    private final LiveCandleCache liveCandleCache;
    private KiteTicker ticker;

    public void connectTicker(List<Long> tokens) {
        try {
            String accessToken = kiteService.getKiteConnect().getAccessToken();
            String apiKey = kiteService.getApiKey();

            ticker = new KiteTicker(accessToken, apiKey);
            
            ticker.setOnConnectedListener(() -> {
                log.info("Ticker Connected. Subscribing to {} tokens", tokens.size());
                if (!tokens.isEmpty()) {
                    ticker.subscribe(new ArrayList<>(tokens));
                    ticker.setMode(new ArrayList<>(tokens), KiteTicker.modeFull);
                }
            });

            ticker.setOnTickerArrivalListener(ticks -> {
                if (ticks != null && !ticks.isEmpty()) {
                    ticks.forEach(liveCandleCache::updateTick);
                }
            });

            ticker.setTryReconnection(true);
            ticker.connect();
            
        } catch (Exception e) {
            log.error("Critical error in Ticker: {}", e.getMessage());
        }
    }
    
    public void disconnect() {
        if (ticker != null) ticker.disconnect();
    }
}