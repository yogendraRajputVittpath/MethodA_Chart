package com.CandleData.dto.index;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndexResponse {

    private String indexName;
    private String tradingSymbol;
    private Double lastPrice;
    private String exchange;
}