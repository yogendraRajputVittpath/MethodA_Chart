package com.CandleData.service.stock;

import com.CandleData.dto.stock.StockResponse;
import com.CandleData.entity.stock.Stock;
import com.CandleData.repository.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockReadService {

    private final StockRepository stockRepository;

    public List<StockResponse> getAllActiveStocks() {

        try {
            log.info("Fetching all active stocks from DB");

            List<Stock> stocks = stockRepository.findAll()
                    .stream()
                    .filter(s -> s.getStatus() == 1)
                    .collect(Collectors.toList());

            return mapToResponseList(stocks);

        } catch (Exception e) {
            log.error("Error fetching active stocks", e);
            throw new RuntimeException("Failed to fetch stocks");
        }
    }
    public StockResponse getStockBySymbol(String symbol) {

        try {
            log.info("Fetching stock for symbol: {}", symbol);

            Stock stock = stockRepository
                    .findByTradingSymbolAndStatus(symbol.toUpperCase(), 1)
                    .orElseThrow(() -> new RuntimeException("Stock not found"));

            return mapToResponse(stock);

        } catch (Exception e) {
            log.error("Error fetching stock: {}", symbol, e);
            throw e;
        }
    }

    private List<StockResponse> mapToResponseList(List<Stock> stocks) {
        return stocks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StockResponse mapToResponse(Stock stock) {
        return StockResponse.builder()
                .tradingSymbol(stock.getTradingSymbol())
                .exchange(stock.getExchange())
                .instrumentToken(stock.getInstrumentToken())
                .lastPrice(stock.getLastPrice())
                .build();
    }
}