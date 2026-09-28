package com.CandleData.repository.HistoricalData;

import com.CandleData.entity.HistoricalData.HistoricalData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistoricalDataRepository {

    @PersistenceContext
    private final EntityManager entityManager;
    
    @Transactional
    public void deleteOlderThan(String interval, String tradingSymbol, Date cutoffDate) {

        String tableName = "`" + interval + "_historicaldata_eq`";

        String sql = String.format(
                "DELETE FROM %s WHERE trading_symbol = :symbol AND time_stamp < :cutoff",
                tableName
        );

        entityManager.createNativeQuery(sql)
                .setParameter("symbol", tradingSymbol)
                .setParameter("cutoff", cutoffDate)
                .executeUpdate();
    }

    @Transactional
    public void saveBatch(String interval, List<HistoricalData> dataList) {
        if (dataList == null || dataList.isEmpty()) return;

        String tableName = "`" + interval + "_historicaldata_eq`";

        String sql = String.format("""
            INSERT IGNORE INTO %s 
            (id, time_stamp, trading_symbol, instrument_token, open_price, high_price, low_price, close_price, volume, oi) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, tableName);

        int batchSize = 1000;

        for (int i = 0; i < dataList.size(); i++) {
            HistoricalData d = dataList.get(i);

            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, d.getId());
            query.setParameter(2, d.getTimeStamp());
            query.setParameter(3, d.getTradingSymbol());
            query.setParameter(4, d.getInstrumentToken());
            query.setParameter(5, d.getOpen());
            query.setParameter(6, d.getHigh());
            query.setParameter(7, d.getLow());
            query.setParameter(8, d.getClose());
            query.setParameter(9, d.getVolume());
            query.setParameter(10, d.getOi());
            
            query.executeUpdate();

            // Clear persistence context to prevent OutOfMemory for large lists
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
    public List<Object[]> fetchLatest(String interval, Long token, Date from, Date to) {

        String tableName = "`" + interval + "_historicaldata_eq`";

        String sql = String.format("""
            SELECT time_stamp, open_price, high_price, low_price, close_price, volume
            FROM %s
            WHERE instrument_token = :token
            AND time_stamp BETWEEN :from AND :to
            ORDER BY time_stamp ASC
            """, tableName);

        return entityManager.createNativeQuery(sql)
                .setParameter("token", token)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}