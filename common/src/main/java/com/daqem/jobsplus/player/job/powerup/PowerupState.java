package com.daqem.jobsplus.player.job.powerup;

public enum PowerupState {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    NOT_OWNED("Not Owned"),
    LOCKED("Locked");

    private final String state;

    PowerupState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
