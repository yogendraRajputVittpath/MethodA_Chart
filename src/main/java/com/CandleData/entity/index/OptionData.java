package com.CandleData.entity.index;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

import com.CandleData.entity.MarketEntity;

@Entity
@Table(name = "index_options", indexes = {
    @Index(name = "idx_index_name", columnList = "indexName"),
    @Index(name = "idx_expiry", columnList = "expiry")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionData implements MarketEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer strike;       // e.g., "22500"
    private String optionType;   // "CE" or "PE"
    private Date expiry;         
    private String tradingSymbol; // e.g., "NIFTY24MAR22500CE"
    private Long instrumentToken;
    private String indexName;    // Mapping ke liye (NIFTY 50, etc.)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id")
    private IndexData parentIndex;
}