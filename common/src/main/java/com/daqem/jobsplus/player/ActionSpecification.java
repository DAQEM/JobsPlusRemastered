package com.daqem.jobsplus.player;

import com.daqem.jobsplus.player.job.Job;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record ActionSpecification<T>(ResourceLocation location) {

    public static final ActionSpecification<BlockState> BLOCK_STATE = create("block_state");
    public static final ActionSpecification<BlockPos> BLOCK_POSITION = create("block_position");
    public static final ActionSpecification<Integer> EXP_DROP = create("exp_drop");
    public static final ActionSpecification<Integer> EXP_LEVEL = create("exp_level");
    public static final ActionSpecification<Level> WORLD = create("world");
    public static final ActionSpecification<DamageSource> DAMAGE_SOURCE = create("damage_source");
    public static final ActionSpecification<Integer> JOB_EXP = create("job_exp");
    public static final ActionSpecification<Entity> ENTITY = create("entity");
    public static final ActionSpecification<Float> DAMAGE_AMOUNT = create("damage_amount");
    public static final ActionSpecification<Integer> DISTANCE_IN_CM = create("swimming_distance_in_cm");
    public static final ActionSpecification<Job> ONLY_FOR_JOB = create("only_for_job");
    public static final ActionSpecification<ItemStack> ITEM_STACK = create("item_stack");
    public static final ActionSpecification<Item> ITEM = create("item");
    public static final ActionSpecification<Container> CONTAINER = create("container");
    public static final ActionSpecification<Advancement> ADVANCEMENT = create("advancement");
    public static final ActionSpecification<MobEffectInstance> MOB_EFFECT_INSTANCE = create("mob_effect_instance");
    public static final ActionSpecification<Recipe<?>> RECIPE = create("recipe");

    private static <T> ActionSpecification<T> create(String location) {
        return new ActionSpecification<>(new ResourceLocation(location));
    }
}
