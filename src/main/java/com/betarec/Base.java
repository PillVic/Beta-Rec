package com.betarec;

import static com.betarec.utils.ObjectAnalyzer.toJsonString;

public class Base {
    @Override
    public String toString(){
        return toJsonString(this);
    }
}
