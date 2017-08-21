package me.aflak.utils;

/**
 * Created by root on 19/08/17.
 */

public interface Condition<T> {
    boolean verify(T object);
}
