package com.CandleData.controller.stock;

import com.CandleData.dto.stock.StockResponse;
import com.CandleData.service.stock.StockReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Slf4j
public class StockReadController {

    private final StockReadService stockReadService;

    @GetMapping
    public ResponseEntity<?> getAllStocks() {

        try {
            List<StockResponse> stocks = stockReadService.getAllActiveStocks();

            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "data", stocks
                    )
            );

        } catch (Exception e) {

            log.error("Failed to fetch stocks", e);

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "status", "ERROR",
                            "message", e.getMessage()
                    )
            );
        }
    }
    
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStock(@PathVariable String symbol) {

        try {
            StockResponse stock = stockReadService.getStockBySymbol(symbol);
            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "data", stock)                                      
                          );
            
        } catch (RuntimeException e) {
            log.error("Stock not found: {}", symbol);
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "message", e.getMessage() )    
                         );
        }
        catch (Exception e) {
            log.error("Error fetching stock: {}", symbol, e);
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "status", "ERROR",
                            "message", "Failed to fetch stock")
            );
        }
    }
}