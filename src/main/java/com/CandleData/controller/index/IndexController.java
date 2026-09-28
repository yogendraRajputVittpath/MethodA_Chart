package com.CandleData.controller.index;

import com.CandleData.entity.index.IndexData;
import com.CandleData.service.index.IndexService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/indices")
@Slf4j
public class IndexController {

    private final IndexService indexService;

    @GetMapping("/sync")
    public ResponseEntity<?> syncIndices() throws KiteException {

        try {
 
            List<IndexData> data = indexService.syncTopIndices();

            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "count", data.size(),
                            "data", data 
                           )
                      );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "status", "ERROR",
                            "message", e.getMessage()
                    )
            );
        }
    }
  }