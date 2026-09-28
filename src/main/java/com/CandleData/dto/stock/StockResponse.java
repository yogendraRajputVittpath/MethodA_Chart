package com.CandleData.dto.stock;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockResponse {

    private String tradingSymbol;
    private String exchange;
    private Long instrumentToken;
    private Double lastPrice;
}