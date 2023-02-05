package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;

public class StatEvents {

    public static void onAwardStat(JobsServerPlayer player, Stat<?> stat, int previousAmount, int newAmount) {
        onAwardSwimStat(player, stat, newAmount);
        onAwardUseStat(player, stat, newAmount);
    }

    private static void onAwardSwimStat(JobsServerPlayer player, Stat<?> stat, int newAmount) {
        if (stat.equals(Stats.CUSTOM.get(Stats.SWIM_ONE_CM))) {
            player.setSwimmingDistanceInCm(newAmount);
        }
    }

    private static void onAwardUseStat(JobsServerPlayer player, Stat<?> stat, int newAmount) {
        if (stat.getType() == Stats.ITEM_USED) {
            ItemEvents.onUseItem(player, (Item) stat.getValue());
        }
    }
}
