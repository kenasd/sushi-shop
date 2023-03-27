package org.alex.service.cooking;

import org.alex.model.SushiOrder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CookTime {
    private final SushiOrder sushiOrder;
    private final AtomicInteger timeSpent;
    private final AtomicLong lastTimeUpdate;
    private boolean ready;

    public CookTime(SushiOrder sushiOrder) {
        this.sushiOrder = sushiOrder;
        this.timeSpent = new AtomicInteger(0);
        this.lastTimeUpdate = new AtomicLong(0);
        this.ready = false;
    }

    public int getOrderId() {
        return sushiOrder.getId();
    }

    public SushiOrder getSushiOrder() {
        return sushiOrder;
    }

    public int getTimeMax() {
        return sushiOrder.getSushi().getTimeToMake();
    }

    public int getTimeSpent() {
        return timeSpent.intValue();
    }

    public String getStatusName() {
        return sushiOrder.getStatus().getName();
    }

    public long getLastTimeUpdate() {
        return lastTimeUpdate.longValue();
    }

    public boolean isReady() {
        return ready;
    }

    public void setLastTimeUpdate(long lastTimeUpdate) {
        this.lastTimeUpdate.set(lastTimeUpdate);
    }

    public void addTimeSpent(int delta) {
        if (!ready) {
            timeSpent.getAndAdd(delta);
            if (getTimeSpent() > getTimeMax()) {
                ready = true;
                timeSpent.set(getTimeMax());
            }
        }
    }
}
