package com.jaisel.tictactoe.Utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static Map<String, Object> from(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
