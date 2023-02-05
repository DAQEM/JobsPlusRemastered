package com.daqem.jobsplus.player;

import com.daqem.jobsplus.player.job.Job;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.swing.text.html.parser.Entity;

public record ActionSpecification<T>(ResourceLocation location) {

    public static final ActionSpecification<BlockState> BLOCK_STATE = create("block_state");
    public static final ActionSpecification<BlockPos> BLOCK_POSITION = create("block_position");
    public static final ActionSpecification<Integer> BLOCK_EXP_DROP = create("block_exp_drop");
    public static final ActionSpecification<Level> WORLD = create("world");
    public static final ActionSpecification<DamageSource> DAMAGE_SOURCE = create("damage_source");
    public static final ActionSpecification<Integer> JOB_EXP = create("job_exp");
    public static final ActionSpecification<Entity> ENTITY = create("entity");
    public static final ActionSpecification<Float> DAMAGE_AMOUNT = create("damage_amount");
    public static final ActionSpecification<Integer> SWIMMING_DISTANCE_IN_CM = create("swimming_distance_in_cm");
    public static final ActionSpecification<Job> ONLY_FOR_JOB = create("only_for_job");
    public static final ActionSpecification<ItemStack> ITEM_STACK = create("item_stack");
    public static final ActionSpecification<Item> ITEM = create("item");
    public static final ActionSpecification<Container> CONTAINER = create("container");
    public static final ActionSpecification<Advancement> ADVANCEMENT = create("advancement");

    private static <T> ActionSpecification<T> create(String location) {
        return new ActionSpecification<>(new ResourceLocation(location));
    }
}
