package com.daqem.jobsplus.util.experience;

public class ExperienceHandler {

    public static int getMaxExperienceForLevel(int level) {
        if (level == 0) return 0;
        return (int) (100 + level * level * 0.5791);
    }
}
