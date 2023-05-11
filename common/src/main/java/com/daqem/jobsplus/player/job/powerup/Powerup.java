package com.daqem.jobsplus.player.job.powerup;

import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Powerup {

    private @Nullable Powerup parent;
    private final List<Powerup> children = new ArrayList<>();

    private final PowerupInstance powerupInstance;
    private PowerupState powerupState;

    public Powerup(PowerupInstance powerupInstance, PowerupState powerupState) {
        this.powerupInstance = powerupInstance;
        this.powerupState = powerupState;
    }

    public PowerupInstance getPowerupInstance() {
        return powerupInstance;
    }

    public PowerupState getPowerupState() {
        return powerupState;
    }

    public void setPowerupState(PowerupState powerupState) {
        this.powerupState = powerupState;
    }

    public List<Powerup> getChildren() {
        return children;
    }

    @Nullable
    public Powerup getParent() {
        return parent;
    }

    public void setParent(@Nullable Powerup parent) {
        this.parent = parent;
    }

    public void toggle() {
        if (powerupState == PowerupState.ACTIVE) {
            powerupState = PowerupState.INACTIVE;
        } else if (powerupState == PowerupState.INACTIVE) {
            powerupState = PowerupState.ACTIVE;
        }
    }

    @Override
    public String toString() {
        JsonObject json = new JsonObject();
        json.add("powerupInstance", GsonHelper.parse(powerupInstance.toShortString()));
        json.addProperty("powerupState", powerupState.name());
        json.addProperty("parent", parent == null ? "null" : parent.getPowerupInstance().getLocation().toString());
        JsonArray children = new JsonArray();
        for (Powerup powerup : getChildren()) {
            children.add(GsonHelper.parse(powerup.toString()));
        }
        json.add("children", children);
        return json.toString();
    }

}
