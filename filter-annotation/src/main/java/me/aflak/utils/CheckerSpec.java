package me.aflak.utils;

/**
 * Created by root on 16/08/17.
 */

public class CheckerSpec {
    private String comparator;
    private Class[] compared;

    public CheckerSpec(String comparator, Class[] compared) {
        this.comparator = comparator;
        this.compared = compared;
    }

    public static CheckerSpec create(String comparator, Class ...compared){
        return new CheckerSpec(comparator, compared);
    }

    public String getComparator() {
        return comparator;
    }

    public Class[] getCompared() {
        return compared;
    }
}
