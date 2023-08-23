package com.daqem.jobsplus.player.job.exp;

public class ExpCollector {

    private int jobExp = 0;

    public void addExp(int exp) {
        jobExp += exp;
    }

    public int getExp() {
        return jobExp;
    }

    public void clear() {
        jobExp = 0;
    }
}
