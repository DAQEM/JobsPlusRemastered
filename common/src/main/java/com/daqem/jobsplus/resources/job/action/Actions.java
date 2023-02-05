package com.daqem.jobsplus.resources.job.action;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.actions.advancement.AdvancementAction;
import com.daqem.jobsplus.resources.job.action.actions.block.BlockBreakAction;
import com.daqem.jobsplus.resources.job.action.actions.block.BlockInteractAction;
import com.daqem.jobsplus.resources.job.action.actions.block.BlockPlaceAction;
import com.daqem.jobsplus.resources.job.action.actions.entity.DeathAction;
import com.daqem.jobsplus.resources.job.action.actions.entity.GetHurtAction;
import com.daqem.jobsplus.resources.job.action.actions.entity.HurtEntityAction;
import com.daqem.jobsplus.resources.job.action.actions.entity.KillEntityAction;
import com.daqem.jobsplus.resources.job.action.actions.item.CraftItemAction;
import com.daqem.jobsplus.resources.job.action.actions.item.DropItemAction;
import com.daqem.jobsplus.resources.job.action.actions.item.ThrowItemAction;
import com.daqem.jobsplus.resources.job.action.actions.item.UseItemAction;
import com.daqem.jobsplus.resources.job.action.actions.job.JobExpAction;
import com.daqem.jobsplus.resources.job.action.actions.job.JobLevelUpAction;
import com.daqem.jobsplus.resources.job.action.actions.movement.*;
import com.daqem.jobsplus.resources.job.action.actions.player.BrewPotionAction;
import com.daqem.jobsplus.resources.job.action.actions.player.DrinkAction;
import com.daqem.jobsplus.resources.job.action.actions.player.EatAction;
import com.daqem.jobsplus.resources.job.action.actions.player.ShootProjectileAction;
import com.google.gson.JsonDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class Actions {

    public static final List<ActionType> ACTION_TYPES = new ArrayList<>();

    public static final ActionType BLOCK_PLACE = register(JobsPlus.getId("on_block_place"), BlockPlaceAction.class, new BlockPlaceAction.Serializer());
    public static final ActionType BLOCK_BREAK = register(JobsPlus.getId("on_block_break"), BlockBreakAction.class, new BlockBreakAction.Serializer());
    public static final ActionType BLOCK_INTERACT = register(JobsPlus.getId("on_block_interact"), BlockInteractAction.class, new BlockInteractAction.Deserializer());
    public static final ActionType JOB_EXP = register(JobsPlus.getId("on_job_exp"), JobExpAction.class, new JobExpAction.Serializer());
    public static final ActionType JOB_LEVEL_UP = register(JobsPlus.getId("on_job_level_up"), JobLevelUpAction.class, new JobLevelUpAction.Deserializer());
    public static final ActionType DEATH = register(JobsPlus.getId("on_death"), DeathAction.class, new DeathAction.Serializer());
    public static final ActionType GET_HURT = register(JobsPlus.getId("on_get_hurt"), GetHurtAction.class, new GetHurtAction.Serializer());
    public static final ActionType KILL_ENTITY = register(JobsPlus.getId("on_kill_entity"), KillEntityAction.class, new KillEntityAction.Serializer());
    public static final ActionType HURT_ENTITY = register(JobsPlus.getId("on_hurt_entity"), HurtEntityAction.class, new HurtEntityAction.Serializer());
    public static final ActionType SWIM = register(JobsPlus.getId("on_swim"), SwimAction.class, new SwimAction.Deserializer());
    public static final ActionType SWIM_START = register(JobsPlus.getId("on_swim_start"), SwimStartAction.class, new SwimStartAction.Deserializer());
    public static final ActionType SWIM_STOP = register(JobsPlus.getId("on_swim_stop"), SwimStopAction.class, new SwimStopAction.Deserializer());
    public static final ActionType CRAFT_ITEM = register(JobsPlus.getId("on_craft_item"), CraftItemAction.class, new CraftItemAction.Deserializer());
    public static final ActionType DROP_ITEM = register(JobsPlus.getId("on_drop_item"), DropItemAction.class, new DropItemAction.Deserializer());
    public static final ActionType USE_ITEM = register(JobsPlus.getId("on_use_item"), UseItemAction.class, new UseItemAction.Deserializer());
    public static final ActionType ADVANCEMENT = register(JobsPlus.getId("on_advancement"), AdvancementAction.class, new AdvancementAction.Deserializer());
    public static final ActionType EAT = register(JobsPlus.getId("on_eat"), EatAction.class, new EatAction.Deserializer());
    public static final ActionType DRINK = register(JobsPlus.getId("on_drink"), DrinkAction.class, new DrinkAction.Deserializer());
    public static final ActionType THROW_ITEM = register(JobsPlus.getId("on_throw_item"), ThrowItemAction.class, new ThrowItemAction.Deserializer());
    public static final ActionType SHOOT_PROJECTILE = register(JobsPlus.getId("on_shoot_projectile"), ShootProjectileAction.class, new ShootProjectileAction.Deserializer());
    public static final ActionType BREW_POTION = register(JobsPlus.getId("on_brew_potion"), BrewPotionAction.class, new BrewPotionAction.Deserializer());
    public static final ActionType WALK = register(JobsPlus.getId("on_walk"), WalkAction.class, new WalkAction.Deserializer());
    public static final ActionType WALK_START = register(JobsPlus.getId("on_walk_start"), WalkStartAction.class, new WalkStartAction.Deserializer());
    public static final ActionType WALK_STOP = register(JobsPlus.getId("on_walk_stop"), WalkStopAction.class, new WalkStopAction.Deserializer());
    public static final ActionType SPRINT = register(JobsPlus.getId("on_sprint"), SprintAction.class, new SprintAction.Deserializer());
    public static final ActionType SPRINT_START = register(JobsPlus.getId("on_sprint_start"), SprintStartAction.class, new SprintStartAction.Deserializer());
    public static final ActionType SPRINT_STOP = register(JobsPlus.getId("on_sprint_stop"), SprintStopAction.class, new SprintStopAction.Deserializer());
    public static final ActionType CROUCH = register(JobsPlus.getId("on_crouch"), CrouchAction.class, new CrouchAction.Deserializer());
    public static final ActionType CROUCH_START = register(JobsPlus.getId("on_crouch_start"), CrouchStartAction.class, new CrouchStartAction.Deserializer());
    public static final ActionType CROUCH_STOP = register(JobsPlus.getId("on_crouch_stop"), CrouchStopAction.class, new CrouchStopAction.Deserializer());
    public static final ActionType ELYTRA_FLY = register(JobsPlus.getId("on_elytra_fly"), ElytraFlyAction.class, new ElytraFlyAction.Deserializer());
    public static final ActionType ELYTRA_FLY_START = register(JobsPlus.getId("on_elytra_fly_start"), ElytraFlyStartAction.class, new ElytraFlyStartAction.Deserializer());
    public static final ActionType ELYTRA_FLY_STOP = register(JobsPlus.getId("on_elytra_fly_stop"), ElytraFlyStopAction.class, new ElytraFlyStopAction.Deserializer());

    private static <T extends Action> ActionType register(ResourceLocation location, Class<T> clazz, JsonDeserializer<? extends Action> deserializer) {
        ActionType actionType = new ActionType(clazz, location, deserializer);
        ACTION_TYPES.add(actionType);
        return Registry.register(JobsPlusRegistry.ACTION, location, actionType);
    }

    public static Class<? extends Action> getClass(ResourceLocation location) {
        ActionType actionType = JobsPlusRegistry.ACTION.get(location);
        if (actionType == null) {
            JobManager.LOGGER.error("Unknown action type: {}", location.toString());
        }
        return actionType.clazz();
    }
}
