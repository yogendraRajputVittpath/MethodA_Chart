package com.CandleData.controller.index;

import com.CandleData.service.index.IndexReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/indices")
@RequiredArgsConstructor
@Slf4j
public class IndexReadController {

    private final IndexReadService indexReadService;

    @GetMapping
    public ResponseEntity<?> getAllIndices() {

        try {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "data", indexReadService.getAllActiveIndices()
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching indices", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<?> getIndex(@PathVariable String symbol) {

        try {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "data", indexReadService.getIndexBySymbol(symbol)
                    )
            );
        } catch (Exception e) {
            log.error("Index not found {}", symbol);
            return ResponseEntity.badRequest().body(
                    Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }
}