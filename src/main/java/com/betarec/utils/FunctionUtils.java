package com.betarec.utils;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class FunctionUtils {
    public static <T> List<T> topN(List<T> lst, int limit, Comparator<T> bigComparator) {
        //remove min, keep big element
        PriorityQueue<T> heap = new PriorityQueue<>(limit, bigComparator.reversed());
        for (T e : lst) {
            heap.offer(e);
            if (heap.size() > limit) {
                heap.poll();
            }
        }
        return heap.stream().sorted(bigComparator).toList();
    }
}
