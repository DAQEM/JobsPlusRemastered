package com.daqem.jobsplus.interation.arc.reward.serializer;

import com.daqem.arc.api.reward.IReward;
import com.daqem.arc.api.reward.serializer.IRewardSerializer;
import com.daqem.arc.api.reward.serializer.RewardSerializer;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.reward.rewards.job.JobExpMultiplierReward;
import com.daqem.jobsplus.interation.arc.reward.rewards.job.JobExpReward;

public interface JobsPlusRewardSerializer<T extends IReward> extends RewardSerializer<T> {

    IRewardSerializer<JobExpReward> JOB_EXP = RewardSerializer.register(JobsPlus.getId("job_exp"), new JobExpReward.Serializer());
    IRewardSerializer<JobExpMultiplierReward> JOB_EXP_MULTIPLIER = RewardSerializer.register(JobsPlus.getId("job_exp_multiplier"), new JobExpMultiplierReward.Serializer());

    static void init() {
    }
}
