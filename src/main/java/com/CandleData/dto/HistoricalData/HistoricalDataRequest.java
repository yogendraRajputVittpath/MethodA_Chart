package com.CandleData.dto.HistoricalData;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class HistoricalDataRequest {

    @NotNull(message = "instrumentToken is mandatory") 
    private Long instrumentToken;

    @NotNull(message = "fromDate is mandatory")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fromDate;

    @NotNull(message = "toDate is mandatory")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date toDate;

    @NotBlank(message = "interval is mandatory")
    private String interval;

    private String tradingSymbol; 
}