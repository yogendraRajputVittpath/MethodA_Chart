package com.CandleData.service.index;

import org.springframework.stereotype.Component;
import com.CandleData.constant.OptionConstants;
import java.util.ArrayList;
import java.util.List;

@Component
public class StrikeCalculator {

    public List<Integer> calculateStrikes(Double ltp, String indexName) {
        List<Integer> strikes = new ArrayList<>();
        if (ltp == null || ltp <= 0) return strikes;

        // Step size constant se uthayein (Default 50)
        int step = OptionConstants.INDEX_STEP_SIZE.getOrDefault(indexName, 50);
        
        // ATM Calculate karein
        int atm = (int) (Math.round(ltp / step) * step);

        // Range fix karein
        int range = indexName.contains("BANK") 
                    ? OptionConstants.RANGE_BANKNIFTY 
                    : OptionConstants.RANGE_DEFAULT;

        for (int i = -range; i <= range; i++) {
            strikes.add(atm + (i * step));
        }
        return strikes;
    }
}