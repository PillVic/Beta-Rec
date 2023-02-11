package com.betarec.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StatCount {
    private final ConcurrentMap<String, Integer> countMap;
    public StatCount(){
        countMap = new ConcurrentHashMap<>();
    }

    public void count(String key){
        countMap.merge(key, 1, Integer::sum);
    }

    @Override
    public String toString(){
        return ObjectAnalyzer.toJsonStringV2(this);
    }
}