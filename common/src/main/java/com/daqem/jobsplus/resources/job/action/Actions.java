package com.daqem.jobsplus.resources.job.action;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.exception.UnknownActionTypeException;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.job.action.actions.advancement.AdvancementAction;
import com.daqem.jobsplus.resources.job.action.actions.block.*;
import com.daqem.jobsplus.resources.job.action.actions.entity.*;
import com.daqem.jobsplus.resources.job.action.actions.item.CraftItemAction;
import com.daqem.jobsplus.resources.job.action.actions.item.DropItemAction;
import com.daqem.jobsplus.resources.job.action.actions.item.ThrowItemAction;
import com.daqem.jobsplus.resources.job.action.actions.item.UseItemAction;
import com.daqem.jobsplus.resources.job.action.actions.job.JobExpAction;
import com.daqem.jobsplus.resources.job.action.actions.job.JobLevelUpAction;
import com.daqem.jobsplus.resources.job.action.actions.movement.*;
import com.daqem.jobsplus.resources.job.action.actions.player.*;
import com.google.gson.JsonDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class Actions {

    public static final List<ActionType> ACTION_TYPES = new ArrayList<>();

    public static final ActionType SWIM = register(JobsPlus.getId("on_swim"), SwimAction.class, new SwimAction.Deserializer());
    public static final ActionType SWIM_START = register(JobsPlus.getId("on_swim_start"), SwimStartAction.class, new SwimStartAction.Deserializer());
    public static final ActionType SWIM_STOP = register(JobsPlus.getId("on_swim_stop"), SwimStopAction.class, new SwimStopAction.Deserializer());
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

    public static final ActionType PLACE_BLOCK = register(JobsPlus.getId("on_place_block"), PlaceBlockAction.class, new PlaceBlockAction.Deserializer());
    public static final ActionType BREAK_BLOCK = register(JobsPlus.getId("on_break_block"), BreakBlockAction.class, new BreakBlockAction.Deserializer());
    public static final ActionType INTERACT_BLOCK = register(JobsPlus.getId("on_interact_block"), InteractBlockAction.class, new InteractBlockAction.Deserializer());
    public static final ActionType JOB_EXP = register(JobsPlus.getId("on_job_exp"), JobExpAction.class, new JobExpAction.Deserializer());
    public static final ActionType JOB_LEVEL_UP = register(JobsPlus.getId("on_job_level_up"), JobLevelUpAction.class, new JobLevelUpAction.Deserializer());
    public static final ActionType DEATH = register(JobsPlus.getId("on_death"), DeathAction.class, new DeathAction.Deserializer());
    public static final ActionType GET_HURT = register(JobsPlus.getId("on_get_hurt"), GetHurtAction.class, new GetHurtAction.Deserializer());
    public static final ActionType KILL_ENTITY = register(JobsPlus.getId("on_kill_entity"), KillEntityAction.class, new KillEntityAction.Deserializer());
    public static final ActionType HURT_ENTITY = register(JobsPlus.getId("on_hurt_entity"), HurtEntityAction.class, new HurtEntityAction.Serializer());
    public static final ActionType CRAFT_ITEM = register(JobsPlus.getId("on_craft_item"), CraftItemAction.class, new CraftItemAction.Deserializer());
    public static final ActionType DROP_ITEM = register(JobsPlus.getId("on_drop_item"), DropItemAction.class, new DropItemAction.Deserializer());
    public static final ActionType USE_ITEM = register(JobsPlus.getId("on_use_item"), UseItemAction.class, new UseItemAction.Deserializer());
    public static final ActionType ADVANCEMENT = register(JobsPlus.getId("on_advancement"), AdvancementAction.class, new AdvancementAction.Deserializer());
    public static final ActionType EAT = register(JobsPlus.getId("on_eat"), EatAction.class, new EatAction.Deserializer());
    public static final ActionType DRINK = register(JobsPlus.getId("on_drink"), DrinkAction.class, new DrinkAction.Deserializer());
    public static final ActionType THROW_ITEM = register(JobsPlus.getId("on_throw_item"), ThrowItemAction.class, new ThrowItemAction.Deserializer());
    public static final ActionType SHOOT_PROJECTILE = register(JobsPlus.getId("on_shoot_projectile"), ShootProjectileAction.class, new ShootProjectileAction.Deserializer());
    public static final ActionType BREW_POTION = register(JobsPlus.getId("on_brew_potion"), BrewPotionAction.class, new BrewPotionAction.Deserializer());
    public static final ActionType EFFECT_ADDED = register(JobsPlus.getId("on_effect_added"), EffectAddedAction.class, new EffectAddedAction.Deserializer());
    public static final ActionType SMELT_ITEM = register(JobsPlus.getId("on_smelt_item"), SmeltItemAction.class, new SmeltItemAction.Deserializer());
    public static final ActionType ENCHANT_ITEM = register(JobsPlus.getId("on_enchant_item"), EnchantItemAction.class, new EnchantItemAction.Deserializer());
    public static final ActionType PLANT_CROP = register(JobsPlus.getId("on_plant_crop"), PlantCropAction.class, new PlantCropAction.Deserializer());
    public static final ActionType HARVEST_CROP = register(JobsPlus.getId("on_harvest_crop"), HarvestCropAction.class, new HarvestCropAction.Deserializer());
    public static final ActionType TAME_ANIMAL = register(JobsPlus.getId("on_tame_animal"), TameAnimalAction.class, new TameAnimalAction.Deserializer());
    public static final ActionType INTERACT_ENTITY = register(JobsPlus.getId("on_interact_entity"), InteractEntityAction.class, new InteractEntityAction.Deserializer());
    public static final ActionType BREED_ANIMAL = register(JobsPlus.getId("on_breed_animal"), BreedAnimalAction.class, new BreedAnimalAction.Deserializer());
    public static final ActionType FISHED_UP_ITEM = register(JobsPlus.getId("on_fished_up_item"), FishedUpItemAction.class, new FishedUpItemAction.Deserializer());
    public static final ActionType STRIP_LOG = register(JobsPlus.getId("on_strip_log"), StripLogAction.class, new StripLogAction.Deserializer());

    private static <T extends Action> ActionType register(ResourceLocation location, Class<T> clazz, JsonDeserializer<? extends Action> deserializer) {
        ActionType actionType = new ActionType(clazz, location, deserializer);
        ACTION_TYPES.add(actionType);
        return Registry.register(JobsPlusRegistry.ACTION, location, actionType);
    }

    public static Class<? extends Action> getClass(ResourceLocation location) throws UnknownActionTypeException {
        ActionType actionType = JobsPlusRegistry.ACTION.get(location);
        if (actionType == null) {
            throw new UnknownActionTypeException(location);
        }
        return actionType.clazz();
    }
}
