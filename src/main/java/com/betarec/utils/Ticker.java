package com.betarec.utils;

import java.util.ArrayList;
import java.util.List;

public class Ticker {
    private long currentTs;
    private long cost;
    private List<String> events;

    public Ticker() {
        this.currentTs = System.currentTimeMillis();
        this.cost = 0;
        this.events = new ArrayList<>();
    }

    public void tick(String event) {
        long nowTs = System.currentTimeMillis();
        long currentCost = nowTs - currentTs;
        events.add(String.format("%s %dms", event, currentCost));
        this.currentTs = nowTs;
        this.cost += currentCost;
    }

    @Override
    public String toString() {
        return String.format("%s %dms",String.join(",", events), cost);
    }
}
