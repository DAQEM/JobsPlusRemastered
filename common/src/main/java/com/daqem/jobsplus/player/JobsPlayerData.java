package com.daqem.jobsplus.player;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.player.job.Job;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is used to store the player data in a single object.
 * This is used to make it easier to send the data to the client.
 * NOTE: For client side use only.
 *
 * @param activeJobs   - The list of jobs that the player has at least 1 level in.
 * @param inactiveJobs - The list of jobs that the player has 0 levels in.
 * @param coins        - The amount of coins the player has.
 */
public record JobsPlayerData(List<Job> activeJobs, List<Job> inactiveJobs, int coins) {

    /**
     * This method is used to convert the data from a compound tag to a JobsPlayerData object.
     *
     * @param compoundTag - The compound tag that contains the data.
     * @return - The JobsPlayerData object that was created from the compound tag.
     */
    public static JobsPlayerData fromNBT(CompoundTag compoundTag) {
        int coins = compoundTag.getInt(Constants.COINS);
        List<Job> activeJobs = new ArrayList<>();
        List<Job> inactiveJobs = new ArrayList<>();
        Job.Serializer.fromNBT(null, compoundTag)
                .forEach(job -> {
                    if (job.getLevel() > 0) {
                        activeJobs.add(job);
                    } else {
                        inactiveJobs.add(job);
                    }
                });
        return new JobsPlayerData(activeJobs, inactiveJobs, coins);
    }
}
