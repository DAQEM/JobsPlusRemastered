package com.daqem.jobsplus.level.block;

import com.daqem.jobsplus.player.JobsServerPlayer;

import java.util.UUID;

public interface JobsBrewingStandBlockEntity {

    JobsServerPlayer getPlayer();

    void setPlayer(JobsServerPlayer player);

    UUID getPlayerUUID();

    void setPlayerUUID(UUID playerUUID);

    void setBrewTime(int i);

    boolean[] getPotionBits();

    boolean[] getLastPotionCount();

    void setLastPotionCount(boolean[] bls);
}
