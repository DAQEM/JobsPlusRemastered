package com.daqem.jobsplus.test;

import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupManager;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PowerupManagerForceAddTest {
    private PowerupManager powerupManager;

    @BeforeEach
    public void setUp() {
        powerupManager = new PowerupManager(null, new ArrayList<>());
    }

    @Test
    public void testAddRootPowerup() {
        PowerupInstance rootInstance = new PowerupInstance("root", "", 0, new ArrayList<>(), new ArrayList<>());
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupState rootState = PowerupState.ACTIVE;
        powerupManager.forceAddPowerup(rootInstance, rootState);

        // The powerup should have been added to the powerup list.
        assertEquals(1, powerupManager.getPowerups().size());
        assertEquals(rootInstance, powerupManager.getPowerups().get(0).getPowerupInstance());
        assertEquals(rootState, powerupManager.getPowerups().get(0).getPowerupState());
    }

    @Test
    public void testAddChildPowerup() {
        PowerupInstance rootInstance = new PowerupInstance("root", "", 0, new ArrayList<>(), new ArrayList<>());
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupInstance childInstance = new PowerupInstance("child", "", 0, new ArrayList<>(), new ArrayList<>());
        childInstance.setLocation(new ResourceLocation("test", "child"));
        rootInstance.getChildren().add(childInstance);
        childInstance.setParent(rootInstance);
        PowerupState state = PowerupState.ACTIVE;

        // Add the root powerup first.
        powerupManager.forceAddPowerup(rootInstance, state);

        // Add the child powerup.
        powerupManager.forceAddPowerup(childInstance, state);

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
    public void testChangeStateSingleRootPowerup() {
        PowerupInstance instance = new PowerupInstance("existing", "", 0, new ArrayList<>(), new ArrayList<>());
        instance.setLocation(new ResourceLocation("test", "existing"));
        PowerupState state1 = PowerupState.ACTIVE;
        PowerupState state2 = PowerupState.INACTIVE;

        // Add the powerup once.
        powerupManager.forceAddPowerup(instance, state1);

        // Try to add the powerup again with a different state.
        powerupManager.forceAddPowerup(instance, state2);

        // The powerup should not have been added again.
        assertEquals(1, powerupManager.getPowerups().size());
        assertEquals(1, powerupManager.getAllPowerups().size());
        assertEquals(state2, powerupManager.getPowerup(instance).getPowerupState());
    }

    @Test
    public void testChangeStateMultiplePowerups() {
        PowerupInstance rootInstance = new PowerupInstance("root", "", 0, new ArrayList<>(), new ArrayList<>());
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupInstance child1Instance = new PowerupInstance("child1", "", 0, new ArrayList<>(), new ArrayList<>());
        child1Instance.setLocation(new ResourceLocation("test", "child1"));
        rootInstance.getChildren().add(child1Instance);
        child1Instance.setParent(rootInstance);
        PowerupInstance child2Instance = new PowerupInstance("child2", "", 0, new ArrayList<>(), new ArrayList<>());
        child2Instance.setLocation(new ResourceLocation("test", "child2"));
        rootInstance.getChildren().add(child2Instance);
        child2Instance.setParent(rootInstance);
        PowerupInstance grandchildInstance = new PowerupInstance("grandchild", "", 0, new ArrayList<>(), new ArrayList<>());
        grandchildInstance.setLocation(new ResourceLocation("test", "grandchild"));
        child1Instance.getChildren().add(grandchildInstance);
        grandchildInstance.setParent(child1Instance);
        PowerupState state1 = PowerupState.ACTIVE;
        PowerupState state2 = PowerupState.INACTIVE;

        // Add the powerups.
        powerupManager.forceAddPowerup(rootInstance, state1);
        powerupManager.forceAddPowerup(child1Instance, state1);
        powerupManager.forceAddPowerup(child2Instance, state1);
        powerupManager.forceAddPowerup(grandchildInstance, state1);

        // Try to add the powerup again with a different state.
        powerupManager.forceAddPowerup(child1Instance, state2);
        powerupManager.forceAddPowerup(child2Instance, state2);

        // The powerup should not have been added again.
        assertEquals(1, powerupManager.getPowerups().size());
        assertEquals(4, powerupManager.getAllPowerups().size());
        assertEquals(state2, powerupManager.getPowerup(child1Instance).getPowerupState());
        assertEquals(state2, powerupManager.getPowerup(child2Instance).getPowerupState());
    }

    @Test
    public void testAddMultiplePowerups() {
        PowerupInstance rootInstance = new PowerupInstance("root", "", 0, new ArrayList<>(), new ArrayList<>());
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupInstance child1Instance = new PowerupInstance("child1", "", 0, new ArrayList<>(), new ArrayList<>());
        child1Instance.setLocation(new ResourceLocation("test", "child1"));
        rootInstance.getChildren().add(child1Instance);
        child1Instance.setParent(rootInstance);
        PowerupInstance child2Instance = new PowerupInstance("child2", "", 0, new ArrayList<>(), new ArrayList<>());
        child2Instance.setLocation(new ResourceLocation("test", "child2"));
        rootInstance.getChildren().add(child2Instance);
        child2Instance.setParent(rootInstance);
        PowerupInstance grandchildInstance = new PowerupInstance("grandchild", "", 0, new ArrayList<>(), new ArrayList<>());
        grandchildInstance.setLocation(new ResourceLocation("test", "grandchild"));
        child1Instance.getChildren().add(grandchildInstance);
        grandchildInstance.setParent(child1Instance);
        PowerupState state = PowerupState.ACTIVE;

        // Add all powerups.
        powerupManager.forceAddPowerup(rootInstance, state);
        powerupManager.forceAddPowerup(child1Instance, state);
        powerupManager.forceAddPowerup(child2Instance, state);
        powerupManager.forceAddPowerup(grandchildInstance, state);

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

    @Test
    public void testRemovePowerup() {
        PowerupInstance powerupInstance = new PowerupInstance("root", "", 0, new ArrayList<>(), new ArrayList<>());
        powerupInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupState powerupState = PowerupState.ACTIVE;

        powerupManager.forceAddPowerup(powerupInstance, powerupState);

        assertNotNull(powerupManager.getPowerup(powerupInstance));
        assertEquals(powerupState, powerupManager.getPowerup(powerupInstance).getPowerupState());

        // Remove powerup by setting state to NOT_OWNED
        powerupManager.forceAddPowerup(powerupInstance, PowerupState.NOT_OWNED);

        assertNull(powerupManager.getPowerup(powerupInstance));
    }

    @Test
    public void testRemoveMultiplePowerups() {
        // Create root powerup with two children.
        PowerupInstance rootInstance = new PowerupInstance("root", "", 0, new ArrayList<>(), new ArrayList<>());
        rootInstance.setLocation(new ResourceLocation("test", "root"));
        PowerupState state = PowerupState.ACTIVE;
        PowerupInstance child1Instance = new PowerupInstance("child1", "", 1, new ArrayList<>(), new ArrayList<>());
        child1Instance.setParent(rootInstance);
        child1Instance.setLocation(new ResourceLocation("test", "child1"));
        PowerupInstance child2Instance = new PowerupInstance("child2", "", 2, new ArrayList<>(), new ArrayList<>());
        child2Instance.setParent(rootInstance);
        child2Instance.setLocation(new ResourceLocation("test", "child2"));
        powerupManager.forceAddPowerup(child2Instance, state);
        PowerupInstance grandchildInstance = new PowerupInstance("grandchild", "", 3, new ArrayList<>(), new ArrayList<>());
        grandchildInstance.setParent(child1Instance);
        grandchildInstance.setLocation(new ResourceLocation("test", "grandchild"));
        powerupManager.forceAddPowerup(grandchildInstance, state);

        assertNotNull(powerupManager.getPowerup(rootInstance));
        assertNotNull(powerupManager.getPowerup(child1Instance));
        assertNotNull(powerupManager.getPowerup(child2Instance));
        assertNotNull(powerupManager.getPowerup(grandchildInstance));

        // Remove powerups by setting state to NOT_OWNED
        powerupManager.forceAddPowerup(child1Instance, PowerupState.NOT_OWNED);

        assertNotNull(powerupManager.getPowerup(rootInstance));
        assertNull(powerupManager.getPowerup(child1Instance));
        assertNotNull(powerupManager.getPowerup(child2Instance));
        assertNull(powerupManager.getPowerup(grandchildInstance));

        // Remove root powerup by setting state to NOT_OWNED
        powerupManager.forceAddPowerup(rootInstance, PowerupState.NOT_OWNED);

        assertNull(powerupManager.getPowerup(rootInstance));
        assertNull(powerupManager.getPowerup(child1Instance));
        assertNull(powerupManager.getPowerup(child2Instance));
        assertNull(powerupManager.getPowerup(grandchildInstance));
    }
}

