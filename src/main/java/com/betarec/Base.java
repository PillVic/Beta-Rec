package com.betarec;

import static com.betarec.utils.ObjectAnalyzer.toJsonStringV2;

/** 类的基础的方法实现
 * @author neovic
* */
public class Base {
    @Override
    public String toString() {
        return toJsonStringV2(this);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        return toJsonStringV2(obj).equals(toString());
    }
}
