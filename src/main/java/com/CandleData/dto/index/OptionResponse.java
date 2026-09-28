package com.CandleData.dto.index;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OptionResponse {

    private String tradingSymbol;
    private Integer strike;
    private String optionType;
    private Date expiry;
    private String indexName;
}