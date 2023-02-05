package com.daqem.jobsplus.player.job;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class JobSerializer {

    public static List<Job> fromNBT(JobsServerPlayer player, CompoundTag compoundTag) {
        ListTag listTag = compoundTag.getList(Constants.JOBS, Tag.TAG_COMPOUND);
        List<Job> jobs = new ArrayList<>();

        for (Tag jobTag : listTag) {
            CompoundTag jobNBT = (CompoundTag) jobTag;
            jobs.add(Job.fromNBT(player, jobNBT));
        }

        return jobs;
    }

    public static ListTag toNBT(List<Job> jobs) {

        ListTag jobsListTag = new ListTag();
        for (Job job : jobs) {
            CompoundTag jobNBT = job.toNBT();
            jobsListTag.add(jobNBT);
        }

        return jobsListTag;
    }
}
