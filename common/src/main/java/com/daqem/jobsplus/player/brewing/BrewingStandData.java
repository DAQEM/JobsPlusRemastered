package com.daqem.jobsplus.player.brewing;

import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

import java.util.HashMap;
import java.util.Map;

public class BrewingStandData {

    private final BrewingStandBlockEntity brewingStandBlockEntity;
    private final Map<Integer, JobsServerPlayer> brewingStandItemOwners = new HashMap<>();
    private JobsServerPlayer lastPlayerToInteract;

    public BrewingStandData(BrewingStandBlockEntity brewingStandBlockEntity) {
        this.brewingStandBlockEntity = brewingStandBlockEntity;
    }

    public BrewingStandData(BrewingStandBlockEntity brewingStandBlockEntity, JobsServerPlayer lastPlayerToInteract) {
        this(brewingStandBlockEntity);
        this.lastPlayerToInteract = lastPlayerToInteract;
    }

    public JobsServerPlayer getBrewingStandItemOwner(int slot) {
        if (!brewingStandItemOwners.containsKey(slot)) {
            return null;
        }
        return brewingStandItemOwners.get(slot);
    }

    public Map<Integer, JobsServerPlayer> getBrewingStandItemOwners() {
        return brewingStandItemOwners;
    }

    public void removeBrewingStandItemOwner(int slot) {
        brewingStandItemOwners.remove(slot);
    }

    public void setLastPlayerToInteract(JobsServerPlayer jobsServerPlayer) {
        this.lastPlayerToInteract = jobsServerPlayer;
    }

    public JobsServerPlayer getLastPlayerToInteract() {
        return lastPlayerToInteract;
    }

    public void addBrewingStandItemOwner(int slot, JobsServerPlayer owner) {
        if (brewingStandItemOwners.containsKey(slot)) {
            brewingStandItemOwners.replace(slot, owner);
        } else {
            brewingStandItemOwners.put(slot, owner);
        }
    }
}
