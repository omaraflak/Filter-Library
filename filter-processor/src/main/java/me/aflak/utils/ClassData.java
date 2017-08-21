package me.aflak.utils;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 14/08/17.
 */

public class ClassData {
    private ClassName className;
    private List<FieldInfo> fieldList;

    public ClassData(ClassName className) {
        this.className = className;
        this.fieldList = new ArrayList<>();
    }

    public void add(FieldInfo fieldInfo){
        fieldList.add(fieldInfo);
    }

    public ClassName getClassName() {
        return className;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }
}
