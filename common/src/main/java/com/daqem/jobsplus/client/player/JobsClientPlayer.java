package com.daqem.jobsplus.client.player;

import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import net.minecraft.client.player.LocalPlayer;

import java.util.List;

public interface JobsClientPlayer extends JobsPlayer {

    LocalPlayer jobsplus$getLocalPlayer();

    void jobsplus$replaceJobs(List<Job> jobs);

    void jobsplus$replaceJob(Job job);
}
