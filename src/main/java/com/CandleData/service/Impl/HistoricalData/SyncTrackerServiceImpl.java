package com.CandleData.service.Impl.HistoricalData;

import com.CandleData.dto.HistoricalData.SyncTrackerRequest;
import com.CandleData.entity.HistoricalData.SyncTracker;
import com.CandleData.repository.HistoricalData.SyncTrackerRepository;
import com.CandleData.service.HistoricalData.SyncTrackerService;
import com.CandleData.specificationHistoricalData.SyncTrackerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncTrackerServiceImpl implements SyncTrackerService {

    private final SyncTrackerRepository repository;

    @Override
    public List<SyncTracker> getAllSyncTrackerData(SyncTrackerRequest request) {
        Specification<SyncTracker> spec = SyncTrackerSpecification.getFilterData(request);
        return repository.findAll(spec);
    }
}