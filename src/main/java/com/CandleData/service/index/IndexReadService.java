package com.CandleData.service.index;

import com.CandleData.dto.index.IndexResponse;
import com.CandleData.entity.index.IndexData;
import com.CandleData.repository.index.IndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexReadService {

    private final IndexRepository indexRepository;

    public List<IndexResponse> getAllActiveIndices() {

        try {
            log.info("Fetching all active indices");

            List<IndexData> indices = indexRepository.findByStatus(1);

            return indices.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching indices", e);
            throw new RuntimeException("Failed to fetch indices");
        }
    }

    public IndexResponse getIndexBySymbol(String symbol) {

        try {
            log.info("Fetching index: {}", symbol);

            IndexData index = indexRepository
                    .findByTradingSymbol(symbol)
                    .orElseThrow(() -> new RuntimeException("Index not found"));

            return mapToResponse(index);

        } catch (Exception e) {
            log.error("Error fetching index {}", symbol, e);
            throw e;
        }
    }

    private IndexResponse mapToResponse(IndexData index) {
        return IndexResponse.builder()
                .indexName(index.getIndexName())
                .tradingSymbol(index.getTradingSymbol())
                .lastPrice(index.getLastPrice())
                .exchange(index.getExchange())
                .build();
    }
}