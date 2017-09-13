package me.aflak.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Checker {
    public static final List<CheckerSpec> checkers = new ArrayList<>();
    static {
        checkers.add(CheckerSpec.create("matches", Filter.class, Class.class));
        checkers.add(CheckerSpec.create("regex", Pattern.class));
        checkers.add(CheckerSpec.create("equalsTo", Object.class));
        checkers.add(CheckerSpec.create("contains", CharSequence.class));
        checkers.add(CheckerSpec.create("startsWith", CharSequence.class));
        checkers.add(CheckerSpec.create("endsWith", CharSequence.class));
        checkers.add(CheckerSpec.create("greaterThan", Number.class));
        checkers.add(CheckerSpec.create("smallerThan", Number.class));
        checkers.add(CheckerSpec.create("lengthGreaterThan", Number.class));
        checkers.add(CheckerSpec.create("lengthSmallerThan", Number.class));
        checkers.add(CheckerSpec.create("lengthEqualsTo", Number.class));
        checkers.add(CheckerSpec.create("isTrue"));
        checkers.add(CheckerSpec.create("isFalse"));
        checkers.add(CheckerSpec.create("isNull"));
        checkers.add(CheckerSpec.create("positive"));
        checkers.add(CheckerSpec.create("negative"));
        checkers.add(CheckerSpec.create("zero"));
    }

    private static final String NEED_NUMBER = "Need a number";
    private static final String NEED_TEXT = "Need a text";

    public static boolean matches(Object object, Filter filter, Class<?> type){
        try {
            Method method = type.getMethod("on", Collection.class, Filter.class);
            List<?> result = (List<?>) method.invoke(null, Collections.singleton(object), filter);
            return result.size()!=0;
        }
        catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean regex(Object object, Pattern pattern){
        if(object instanceof CharSequence){
            return pattern.matcher(object.toString()).matches();
        }
        throw new IllegalArgumentException(NEED_TEXT);
    }

    public static boolean equalsTo(Object object, Object o) {
        if (object.getClass().getSuperclass() == Number.class) {
            return ((Number)object).doubleValue()==((Number)o).doubleValue();
        }
        return object.equals(o);
    }

    public static boolean contains(Object object, CharSequence o) {
        if (object instanceof CharSequence) {
            return object.toString().contains(o);
        }
        throw new IllegalArgumentException(NEED_TEXT);
    }

    public static boolean startsWith(Object object, CharSequence o) {
        if (object instanceof CharSequence) {
            return object.toString().startsWith(o.toString());
        }
        throw new IllegalArgumentException(NEED_TEXT);
    }

    public static boolean endsWith(Object object, CharSequence o) {
        if (object instanceof CharSequence) {
            return object.toString().endsWith(o.toString());
        }
        throw new IllegalArgumentException(NEED_TEXT);
    }

    public static boolean greaterThan(Object object, Number o) {
        if (object.getClass().getSuperclass() == Number.class) {
            if (object instanceof Integer) {
                return ((Number)object).intValue() > o.intValue();
            }
            if (object instanceof Double) {
                return ((Number)object).doubleValue() > o.doubleValue();
            }
            if (object instanceof Float) {
                return ((Number)object).floatValue() > o.floatValue();
            }
            if (object instanceof Short) {
                return ((Number)object).shortValue() > o.shortValue();
            }
            if (object instanceof Long) {
                return ((Number)object).longValue() > o.longValue();
            }
            if (object instanceof Byte) {
                return ((Number)object).byteValue() > o.byteValue();
            }
        }
        throw new IllegalArgumentException(NEED_NUMBER);
    }

    public static boolean smallerThan(Object object, Number o) {
        return !(equalsTo(object, o) || greaterThan(object, o));
    }

    public static boolean lengthGreaterThan(Object object, Number o){
        if(object instanceof String){
            return greaterThan(String.valueOf(object).length(), o);
        }
        throw new IllegalArgumentException(NEED_TEXT);
    }

    public static boolean lengthSmallerThan(Object object, Number o){
        return !(lengthGreaterThan(object, o) || lengthEqualsTo(object, o));
    }

    public static boolean lengthEqualsTo(Object object, Number o){
        return equalsTo(String.valueOf(object).length(), o);
    }

    public static boolean isTrue(Object object) {
        return (object instanceof Boolean) && (boolean) object;
    }

    public static boolean isFalse(Object object) {
        return (object instanceof Boolean) && !(boolean) object;
    }

    public static boolean isNull(Object object) {
        return object==null;
    }

    public static boolean positive(Object object) {
        return greaterThan(object, 0) || equalsTo(object, 0);
    }

    public static boolean negative(Object object) {
        return smallerThan(object, 0) || equalsTo(object, 0);
    }

    public static boolean zero(Object object) {
        return equalsTo(object, 0);
    }
}