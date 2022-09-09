package com.betarec.utils;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ObjectAnalyzer {
    private static final Gson GSON = new Gson();
    public static <T> String toJsonString(T t) {
        StringBuilder s = new StringBuilder();
        s.append(String.format("%s{", t.getClass().getName()));
        for (var field : t.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object obj = null;
            try {
                obj = field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (obj != null) {
                s.append(String.format("%s=%s;", field.getName(), obj));
            }
        }
        s.trimToSize();
        s.append("}");
        return s.toString();
    }
    public static <T> String toJsonStringV2(T t) {
        return GSON.toJson(t);
    }

    public static <T> boolean contain(Collection<T> collection, T e) {
        return Optional.ofNullable(collection).orElse(Collections.emptySet()).contains(e);
    }
}
