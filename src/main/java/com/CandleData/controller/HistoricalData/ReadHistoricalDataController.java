package com.CandleData.controller.HistoricalData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.CandleData.dto.HistoricalData.HistoricalDataRequest;
import com.CandleData.service.HistoricalData.ReadHistoricalDataService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/get/historical")
@RequiredArgsConstructor
@Slf4j
public class ReadHistoricalDataController {

    private final ReadHistoricalDataService readhistoricalService;

    @GetMapping("/data")
    public ResponseEntity<?> getHistoricalData(
            @Valid @ModelAttribute HistoricalDataRequest request,
            BindingResult result) {

        log.info("Request received: {}", request);

        // VALIDATION HANDLING
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(err ->
                    errors.put(err.getField(), err.getDefaultMessage())
            );

            log.warn("Validation failed: {}", errors);

            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "errors", errors
            ));
        }
        
        if (request.getFromDate().after(request.getToDate())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "fromDate cannot be after toDate"
            ));
        }

        try {
            Map<String, Object> response = readhistoricalService.getHistoricalData(request);

            String status = (String) response.get("status");

            return switch (status) {
                case "SUCCESS" -> ResponseEntity.ok(response);
                case "NOT_FOUND" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                case "ERROR" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                default -> ResponseEntity.badRequest().body(response);
            };

        } catch (Exception e) {
            log.error("Error while fetching historical data", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", "Something went wrong"
            ));
        }
    }
}