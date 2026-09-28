package com.CandleData.constant;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class OptionConstants {

    private OptionConstants() {}

 // 1. Inhe allow karein taki "Skipping unsupported index" error na aaye
    public static final List<String> ALLOWED_INDEXES = Arrays.asList(
    		"NIFTY 50", 
            "NIFTY BANK", 
            "NIFTY FIN SERVICE", 
            "NIFTY MIDCAP SELECT",
            "NIFTY NEXT 50"
        );
    
 // 2. Zerodha (Kite) ke master data mein jo name hota hai uski mapping
    public static final Map<String, String> INDEX_TO_KITE = Map.of(
    		"NIFTY 50", "NIFTY",
            "NIFTY BANK", "BANKNIFTY",
            "NIFTY FIN SERVICE", "FINNIFTY",
            "NIFTY MIDCAP SELECT", "MIDCPNIFTY",
            "NIFTY NEXT 50", "NIFTYNEXT50"
        ); 
 // 3. Har index ka strike price gap (Step Size)
    public static final Map<String, Integer> INDEX_STEP_SIZE = Map.of(
    		"NIFTY 50", 50,
            "NIFTY BANK", 100,
            "NIFTY FIN SERVICE", 50,
            "NIFTY MIDCAP SELECT", 25,
            "NIFTY NEXT 50", 50
        );
    
    public static final int RANGE_BANKNIFTY = 15;
    public static final int RANGE_DEFAULT = 15;

    public static final int EXPIRY_LIMIT = 4;
}