package com.hiraganaNow;

public class Counters {
    private Counters() {} // Non-instantiable class.

    public static String getHorizontalText(int count, int max, String counter, String missing) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i ++)
            sb.append(counter);
        for(int i = count; i < max; i ++)
            sb.append(missing);
        return sb.toString();
    }

    public static String getVerticalText(int count, int max, char counter, char missing) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < max; i ++) {
            sb.append('\n');
            sb.append(i < count ? counter : missing);
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }
}
