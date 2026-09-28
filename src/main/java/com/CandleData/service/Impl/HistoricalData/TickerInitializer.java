package com.CandleData.service.Impl.HistoricalData;

import com.CandleData.config.kite.AppConfigProperties;
import com.CandleData.repository.index.IndexRepository;
import com.CandleData.repository.index.OptionRepository;
import com.CandleData.repository.stock.StockRepository;
import com.CandleData.service.HistoricalData.DataPreloadService;
import com.CandleData.service.kite.KiteTickerService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TickerInitializer implements ApplicationRunner {

    private final KiteTickerService tickerService;
    private final StockRepository stockRepository;
    private final IndexRepository indexRepository;
    private final OptionRepository optionRepository;
    private final AppConfigProperties config;

    @Override
    public void run(ApplicationArguments args) {
    	
    	log.error("TEST MODE VALUE = {}", config.isTestMode());

        List<Long> tokens = new ArrayList<>();

        if (config.isTestMode()) {

            log.warn("TEST MODE ENABLED (Ticker)");

            // 5 STOCKS
            tokens.addAll(
                    stockRepository.findAll()
                            .stream()
                            .limit(config.getStockLimit())
                            .map(s -> s.getInstrumentToken())
                            .toList()
            );

            // ALL INDEX
            tokens.addAll(
                    indexRepository.findAll()
                            .stream()
                            .map(i -> i.getInstrumentToken())
                            .toList()
            );

            // 5 OPTIONS
            tokens.addAll(
                    optionRepository.findAll()
                            .stream()
                            .limit(config.getOptionLimit())
                            .map(o -> o.getInstrumentToken())
                            .toList()
            );

        } else {

            log.info("PRODUCTION MODE (Ticker)");

            tokens.addAll(
                    stockRepository.findAll()
                            .stream()
                            .map(s -> s.getInstrumentToken())
                            .toList()
            );

            tokens.addAll(
                    indexRepository.findAll()
                            .stream()
                            .map(i -> i.getInstrumentToken())
                            .toList()
            );

            tokens.addAll(
                    optionRepository.findAll()
                            .stream()
                            .map(o -> o.getInstrumentToken())
                            .toList()
            );
        }

        // remove duplicates + safety limit
        tokens = tokens.stream()
                .distinct()
                .limit(config.isTestMode() ? 50 : 3000)
                .toList();

        if (!tokens.isEmpty()) {
            log.info("Connecting Ticker with {} tokens...", tokens.size());
            tickerService.connectTicker(tokens);
        } else {
            log.error("No tokens found! Ticker not started.");
        }
    }
}