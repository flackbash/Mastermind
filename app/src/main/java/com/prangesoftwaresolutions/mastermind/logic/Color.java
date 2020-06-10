package com.prangesoftwaresolutions.mastermind.logic;

import java.util.HashMap;
import java.util.Map;

public enum Color {
    BLUE(0),
    VIOLET(1),
    RED(2),
    ORANGE(3),
    YELLOW(4),
    GREEN(5),
    TURQUOISE(6),
    LIGHT_BLUE(7),
    PURPLE(8);

    private int value;
    private static Map<Integer, Color> map = new HashMap<>();

    Color(int value) {
        this.value = value;
    }

    static {
        for (Color color : Color.values()) {
            map.put(color.value, color);
        }
    }

    public static Color valueOf(int color) {
        return map.get(color);
    }

    public int getValue() {
        return value;
    }
}