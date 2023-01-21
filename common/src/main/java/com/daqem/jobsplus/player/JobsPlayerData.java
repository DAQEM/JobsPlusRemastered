package com.daqem.jobsplus.player;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.JobSerializer;
import com.daqem.jobsplus.player.job.display.JobDisplay;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public record JobsPlayerData(List<Job> activeJobs, List<Job> inactiveJobs, int coins, JobDisplay jobDisplay) {

    public static JobsPlayerData fromPlayer(JobsServerPlayer jobsServerPlayer) {
        return new JobsPlayerData(
                jobsServerPlayer.getJobs(),
                jobsServerPlayer.getInactiveJobs(),
                jobsServerPlayer.getCoins(),
                jobsServerPlayer.getDisplay().orElse(null));
    }

    public static JobsPlayerData fromNBT(CompoundTag compoundTag) {
        int coins = compoundTag.getInt(Constants.COINS);
        JobDisplay jobDisplay = JobDisplay.fromNBT(compoundTag);
        List<Job> activeJobs = new ArrayList<>();
        List<Job> inactiveJobs = new ArrayList<>();
        JobSerializer.fromNBT(compoundTag)
                .forEach(job -> {
                    if (job.getLevel() > 0) {
                        activeJobs.add(job);
                    } else {
                        inactiveJobs.add(job);
                    }
                });
        return new JobsPlayerData(activeJobs, inactiveJobs, coins, jobDisplay);
    }

}
