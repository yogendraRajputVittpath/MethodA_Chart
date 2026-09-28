package com.CandleData.config.kite;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfigProperties {

    private boolean testMode;

    private int stockLimit;
    private int optionLimit;
}