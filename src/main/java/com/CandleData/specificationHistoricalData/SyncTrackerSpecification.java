package com.CandleData.specificationHistoricalData;

import com.CandleData.dto.HistoricalData.SyncTrackerRequest;
import com.CandleData.entity.HistoricalData.SyncTracker;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

@Slf4j
public class SyncTrackerSpecification {

    public static Specification<SyncTracker> getFilterData(SyncTrackerRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getInstrumentToken() != null) {
                predicates.add(cb.equal(root.get("instrumentToken"), request.getInstrumentToken()));
            }

            if (request.getInterval() != null) {
                predicates.add(cb.equal(root.get("interval"), request.getInterval()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getTradingSymbol() != null) {
                predicates.add(cb.equal(root.get("tradingSymbol"), request.getTradingSymbol()));
            }

            log.debug("Filters applied: {}", predicates.size());

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}