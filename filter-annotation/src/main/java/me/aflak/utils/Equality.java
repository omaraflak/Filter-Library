package me.aflak.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 18/08/17.
 */

public class Equality {
    private String attribute;
    private String comparator;
    private List<Object> compared;
    private boolean not;

    public Equality(){
        compared = new ArrayList<>();
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public Object getCompared(int index) {
        if(index<compared.size())
            return compared.get(index);
        throw new IndexOutOfBoundsException("Trie to access index "+index+" where size was "+compared.size());
    }

    public void addCompared(Object ...compared) {
        if(compared!=null) {
            this.compared.addAll(Arrays.asList(compared));
        }
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }
}
