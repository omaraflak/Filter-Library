package me.aflak.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 18/08/17.
 */

public class Filter {
    private List<Equality> equalities;
    private Condition extraCondition;
    private Operation postOperation;
    private boolean copy;

    public Filter(){
        this.equalities = new ArrayList<>();
        this.copy = false;
    }

    public List<Equality> getEqualities() {
        return equalities;
    }

    public void add(Equality equality){
        this.equalities.add(equality);
    }

    public void setEqualities(List<Equality> equalities) {
        this.equalities = equalities;
    }

    public Condition getExtraCondition() {
        return extraCondition;
    }

    public void setExtraCondition(Condition extraCondition) {
        this.extraCondition = extraCondition;
    }

    public Operation getPostOperation() {
        return postOperation;
    }

    public void setPostOperation(Operation postOperation) {
        this.postOperation = postOperation;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }
}
