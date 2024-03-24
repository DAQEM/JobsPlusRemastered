package com.daqem.jobsplus.integration.arc.reward.type;

import com.daqem.arc.api.reward.IReward;
import com.daqem.arc.api.reward.type.IRewardType;
import com.daqem.arc.api.reward.type.RewardType;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.integration.arc.reward.rewards.job.JobExpReward;
import com.daqem.jobsplus.integration.arc.reward.rewards.job.JobExpMultiplierReward;

public interface JobsPlusRewardType<T extends IReward> extends RewardType<T> {

    IRewardType<JobExpReward> JOB_EXP = RewardType.register(JobsPlus.getId("job_exp"), new JobExpReward.Serializer());
    IRewardType<JobExpMultiplierReward> JOB_EXP_MULTIPLIER = RewardType.register(JobsPlus.getId("job_exp_multiplier"), new JobExpMultiplierReward.Serializer());

    static void init() {
    }
}
