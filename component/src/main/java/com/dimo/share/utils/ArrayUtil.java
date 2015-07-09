package com.dimo.share.utils;

import java.lang.reflect.Array;

public class ArrayUtil {
    private ArrayUtil() {
        throw new AssertionError();
    }

    public static String[] join(String[] left, String[] right) {
        if (left == null) {
            return clone(right);
        } else if (right == null) {
            return clone(left);
        }
        String[] joined = (String[]) Array.newInstance(String.class, left.length + right.length);
        System.arraycopy(left, 0, joined, 0, left.length);
        System.arraycopy(right, 0, joined, left.length, right.length);
        return joined;
    }

    public static String[] clone(String[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
}
