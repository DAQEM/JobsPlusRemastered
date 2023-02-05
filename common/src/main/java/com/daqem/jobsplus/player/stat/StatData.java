package com.daqem.jobsplus.player.stat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;

public class StatData {

    private final Stat<?> stat;
    private int amount;

    public StatData(Stat<?> stat, int amount) {
        this.stat = stat;
        this.amount = amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public int getAmount() {
        return amount;
    }

    public Stat<?> getStat() {
        return stat;
    }

    public boolean is(ResourceLocation location) {
        return stat.equals(Stats.CUSTOM.get(location));
    }
}
