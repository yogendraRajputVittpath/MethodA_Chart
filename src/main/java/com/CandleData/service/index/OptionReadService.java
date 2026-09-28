package com.CandleData.service.index;

import com.CandleData.dto.index.OptionResponse;
import com.CandleData.entity.index.OptionData;
import com.CandleData.repository.index.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptionReadService {

    private final OptionRepository optionRepository;

    public List<OptionResponse> getOptionsByIndex(String indexName) {

        try {
            log.info("Fetching options for index: {}", indexName);

            List<OptionData> options =
                    optionRepository.findByIndexName(indexName);
            
            if (options.isEmpty()) {
                log.warn("No options found for index: {}", indexName);
                throw new RuntimeException("Index option not found");
            }

            return options.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching options for {}", indexName, e);
            throw new RuntimeException("Index option not found");
        }
    }

    private OptionResponse mapToResponse(OptionData option) {
        return OptionResponse.builder()
                .tradingSymbol(option.getTradingSymbol())
                .strike(option.getStrike())
                .optionType(option.getOptionType())
                .expiry(option.getExpiry())
                .indexName(option.getIndexName())
                .build();
    }
}