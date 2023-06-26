package com.daqem.jobsplus.test;

import com.daqem.jobsplus.player.job.powerup.JobPowerupManager;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PowerupManagerAddTest {
    private JobPowerupManager powerupManager;

    @BeforeEach
    public void setUp() {
        powerupManager = new JobPowerupManager(null, new ArrayList<>());
    }

    @Test
    public void testAddRootPowerup() {
        PowerupInstance rootInstance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "root", "", 0, 10);
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupState rootState = PowerupState.ACTIVE;
        powerupManager.addPowerup(rootInstance, rootState);

        // The powerup should have been added to the powerup list.
        assertEquals(1, powerupManager.getPowerups().size());
        assertEquals(rootInstance, powerupManager.getPowerups().get(0).getPowerupInstance());
        assertEquals(rootState, powerupManager.getPowerups().get(0).getPowerupState());
    }

    @Test
    public void testAddChildPowerupAfterRootPowerup() {
        PowerupInstance rootInstance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "root", "", 0, 10);
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupInstance childInstance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "child", "", 0, 10);
        childInstance.setLocation(new ResourceLocation("test", "child"));
        rootInstance.getChildren().add(childInstance);
        childInstance.setParent(rootInstance);
        PowerupState state = PowerupState.ACTIVE;

        // Add the root powerup first.
        powerupManager.addPowerup(rootInstance, state);

        // Add the child powerup.
        powerupManager.addPowerup(childInstance, state);

        // Both powerups should have been added to the powerup list.
        assertEquals(1, powerupManager.getPowerups().size());
        assertEquals(2, powerupManager.getAllPowerups().size());

        // The root powerup should have the child powerup as its only child.
        Powerup rootPowerup = powerupManager.getPowerup(rootInstance);
        assertNotNull(rootPowerup);
        assertEquals(1, rootPowerup.getChildren().size());
        assertEquals(childInstance, rootPowerup.getChildren().get(0).getPowerupInstance());
    }

    @Test
    public void testAddChildPowerupBeforeRootPowerup() {
        PowerupInstance rootInstance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "root", "", 0, 10);
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupInstance childInstance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "child", "", 0, 10);
        childInstance.setLocation(new ResourceLocation("test", "child"));
        rootInstance.getChildren().add(childInstance);
        childInstance.setParent(rootInstance);
        PowerupState state = PowerupState.ACTIVE;

        // Add the child powerup first.
        powerupManager.addPowerup(childInstance, state);

        // Add the root powerup.
        powerupManager.addPowerup(rootInstance, state);

        // Both powerups should have been added to the powerup list.
        assertEquals(1, powerupManager.getPowerups().size());
        assertEquals(1, powerupManager.getAllPowerups().size());

        // The root powerup should not have the child powerup because it was added first.
        Powerup rootPowerup = powerupManager.getPowerup(rootInstance);
        assertNotNull(rootPowerup);
        assertEquals(0, rootPowerup.getChildren().size());
    }

    @Test
    public void testAddMultiplePowerups() {
        PowerupInstance rootInstance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "root", "", 0, 10);
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupInstance child1Instance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "child1", "", 0, 10);
        child1Instance.setLocation(new ResourceLocation("test", "child1"));
        rootInstance.getChildren().add(child1Instance);
        child1Instance.setParent(rootInstance);
        PowerupInstance child2Instance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "child2", "", 0, 10);
        child2Instance.setLocation(new ResourceLocation("test", "child2"));
        rootInstance.getChildren().add(child2Instance);
        child2Instance.setParent(rootInstance);
        PowerupInstance grandchildInstance = new PowerupInstance(new ResourceLocation(""), new ResourceLocation(""), "grandchild", "", 0, 10);
        grandchildInstance.setLocation(new ResourceLocation("test", "grandchild"));
        child1Instance.getChildren().add(grandchildInstance);
        grandchildInstance.setParent(child1Instance);
        PowerupState state = PowerupState.ACTIVE;

        // Add all powerups.
        powerupManager.addPowerup(rootInstance, state);
        powerupManager.addPowerup(child1Instance, state);
        powerupManager.addPowerup(child2Instance, state);
        powerupManager.addPowerup(grandchildInstance, state);

        // All powerups should have been added to the powerup list.
        assertEquals(1, powerupManager.getPowerups().size());
        assertEquals(4, powerupManager.getAllPowerups().size());

        // The root powerup should have two children: child1 and child2.
        Powerup rootPowerup = powerupManager.getPowerup(rootInstance);
        assertNotNull(rootPowerup);
        assertEquals(2, rootPowerup.getChildren().size());
        assertTrue(rootPowerup.getChildren().stream()
                .anyMatch(child -> child.getPowerupInstance().equals(child1Instance)));
        assertTrue(rootPowerup.getChildren().stream()
                .anyMatch(child -> child.getPowerupInstance().equals(child2Instance)));

        // The child1 powerup should have one child: grandchild.
        Powerup child1Powerup = powerupManager.getPowerup(child1Instance);
        assertNotNull(child1Powerup);
        assertEquals(1, child1Powerup.getChildren().size());
        assertEquals(grandchildInstance, child1Powerup.getChildren().get(0).getPowerupInstance());
    }
}

