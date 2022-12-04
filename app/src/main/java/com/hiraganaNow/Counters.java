package com.hiraganaNow;

public class Counters {
    private Counters() {} // Non-instantiable class.

    public static String getText(int count, int max, char counterSymbol, char missingSymbol) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < max; i ++) {
            sb.append('\n');
            sb.append(i < count ? counterSymbol : missingSymbol);
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }
}
