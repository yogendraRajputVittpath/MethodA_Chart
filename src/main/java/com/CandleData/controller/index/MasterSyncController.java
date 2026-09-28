package com.CandleData.controller.index;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CandleData.entity.index.IndexData;
import com.CandleData.service.index.IndexService;
import com.CandleData.service.index.OptionSyncService;
import com.CandleData.service.kite.KiteService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Instrument;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master-sync")
public class MasterSyncController {

    private final IndexService indexService;
    private final OptionSyncService optionSyncService;
    private final KiteService kiteService;

    @GetMapping("/run")
    public ResponseEntity<?> runFullSync() throws KiteException {
        try {
            // Step 1: Sync Indices
            List<IndexData> indices = indexService.syncTopIndices();

            // Step 2: Fetch NFO once
            List<Instrument> nfoInstruments =
                    kiteService.getKiteConnect().getInstruments("NFO");

            // Step 3: Sync options
            for (IndexData index : indices) {
                optionSyncService.syncOptions(index, nfoInstruments);
            }

            return ResponseEntity.ok(Map.of("message", "Full Sync Successful"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}