package com.daqem.jobsplus.player.job.display;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.resources.job.JobInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record JobDisplay(JobInstance jobInstance) {

    public String getLocationString() {
        return jobInstance.getLocation().toString();
    }

    public static JobDisplay fromNBT(CompoundTag tag) {
        return tag.getString(Constants.DISPLAY).isEmpty()
                ? null
                : new JobDisplay(
                JobInstance.of(
                        new ResourceLocation(
                                tag.getString(Constants.DISPLAY))));
    }
}
