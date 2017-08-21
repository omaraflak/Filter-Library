package me.aflak.utils;

import com.squareup.javapoet.ClassName;

/**
 * Created by root on 14/08/17.
 */

public class FieldInfo {
    private ClassName fieldClass;
    private String fieldName;
    private Boolean isPrimitive;

    public FieldInfo(ClassName fieldClass, String fieldName, Boolean isPrimitive) {
        this.fieldClass = fieldClass;
        this.fieldName = fieldName;
        this.isPrimitive = isPrimitive;
    }

    public ClassName getFieldClass() {
        return fieldClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Boolean isPrimitive() {
        return isPrimitive;
    }
}
