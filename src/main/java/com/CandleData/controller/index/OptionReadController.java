package com.CandleData.controller.index;

import com.CandleData.service.index.OptionReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
@Slf4j
public class OptionReadController {

    private final OptionReadService optionReadService;

    @GetMapping("/{indexName}")
    public ResponseEntity<?> getOptions(@PathVariable String indexName) {

        try {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "data", optionReadService.getOptionsByIndex(indexName)
                    )
            );
            
        } catch (RuntimeException e) {

            log.error("Options not found for {}", indexName);

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "message", e.getMessage()
                    )
            );
            
        } catch (Exception e) {
            log.error("Error fetching options {}", indexName, e);
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "ERROR", "message", e.getMessage())
            );
        }
    }
}