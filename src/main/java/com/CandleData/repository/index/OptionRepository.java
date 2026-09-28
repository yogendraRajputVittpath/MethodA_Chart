package com.CandleData.repository.index;

import com.CandleData.entity.index.OptionData;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<OptionData, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM OptionData o WHERE o.indexName = :indexName")
    void deleteByIndexName(String indexName);
 
    List<OptionData> findByIndexName(String indexName);
    List<OptionData> findByIndexNameAndExpiry(String indexName, Date expiry);
    List<OptionData> findByExpiryGreaterThanEqual(Date expiry);
}