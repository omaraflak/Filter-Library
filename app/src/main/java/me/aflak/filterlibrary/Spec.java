package me.aflak.filterlibrary;

import me.aflak.filter_annotation.Filterable;

/**
 * Created by root on 18/08/17.
 */

@Filterable
public class Spec {
    private int size;
    private int mass;

    public Spec(int size, int mass) {
        this.size = size;
        this.mass = mass;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMass() {
        return mass;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }
}
