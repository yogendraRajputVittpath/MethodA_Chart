package com.CandleData.service.index;

import com.CandleData.constant.OptionConstants;
import com.CandleData.entity.index.IndexData;
import com.CandleData.entity.index.OptionData;
import com.CandleData.repository.index.OptionRepository;
import com.zerodhatech.models.Instrument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptionSyncService {

    private final OptionRepository optionRepository;
    private final StrikeCalculator strikeCalculator;

    @Transactional
    public void syncOptions(IndexData index, List<Instrument> nfoInstruments) {
        String indexSymbol = index.getTradingSymbol();

        // 1. Validation check
        if (!OptionConstants.ALLOWED_INDEXES.contains(indexSymbol)) {
            log.warn("Skipping unsupported index: {}", indexSymbol);
            return;
        }

        try {
            String kiteName = OptionConstants.INDEX_TO_KITE.get(indexSymbol);
            if (kiteName == null) {
                log.error("Mapping missing for index: {}", indexSymbol);
                return;
            }

            // 2. Filter instruments based on Kite Name (FINNIFTY, MIDCPNIFTY etc)
            List<Instrument> filteredList = nfoInstruments.stream()
                    .filter(inst -> inst.name != null && inst.name.equalsIgnoreCase(kiteName))
                    .filter(inst -> "CE".equals(inst.instrument_type) || "PE".equals(inst.instrument_type))
                    .collect(Collectors.toList());

            if (filteredList.isEmpty()) {
                log.warn("No instruments found for {} (Kite Name: {})", indexSymbol, kiteName);
                return;
            }

            // 3. Get target strikes and expiries
            List<Integer> targetStrikes = strikeCalculator.calculateStrikes(index.getLastPrice(), indexSymbol);
            Set<Integer> strikeSet = new HashSet<>(targetStrikes);

//            Set<Date> expirySet = filteredList.stream()
//                    .map(Instrument::getExpiry)
//                    .filter(Objects::nonNull)
//                    .distinct()
//                    .sorted()
//                    .limit(OptionConstants.EXPIRY_LIMIT)
//                    .collect(Collectors.toCollection(LinkedHashSet::new));
            
            LocalDate today = LocalDate.now();

            Set<Date> expirySet = filteredList.stream()
                    .map(Instrument::getExpiry)
                    .filter(Objects::nonNull)
                    .filter(expiry -> {
                        LocalDate expiryDate = expiry.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        return !expiryDate.isBefore(today);
                    })
                    .distinct()
                    .sorted()
                    .limit(OptionConstants.EXPIRY_LIMIT)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // 4. Map to entity
            List<OptionData> optionsToSave = filteredList.stream()
                    .filter(inst -> expirySet.contains(inst.getExpiry()))
                    .map(inst -> {
                        Integer strike = parseStrike(inst.strike);
                        if (strike == null || !strikeSet.contains(strike)) return null;
                        return mapToEntity(inst, index, strike);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 5. Save to DB
            if (!optionsToSave.isEmpty()) {
                optionRepository.deleteByIndexName(indexSymbol);
                optionRepository.saveAll(optionsToSave);
                log.info("Successfully synced {} options for {}", optionsToSave.size(), indexSymbol);
            }

        } catch (Exception e) {
            log.error("Sync failed for {}: {}", indexSymbol, e.getMessage(), e);
        }
    }

    private OptionData mapToEntity(Instrument inst, IndexData parent, Integer strike) {
        return OptionData.builder()
                .indexName(parent.getTradingSymbol())
                .tradingSymbol(inst.tradingsymbol)
                .instrumentToken(inst.instrument_token)
                .strike(strike)
                .optionType(inst.instrument_type)
                .expiry(inst.expiry)
                .parentIndex(parent)
                .build();
    }

    private Integer parseStrike(String strike) {
        try {
            return (strike == null) ? null : (int) Double.parseDouble(strike);
        } catch (Exception e) {
            return null;
        }
    }
}