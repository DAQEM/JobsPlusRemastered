package com.daqem.jobsplus.command;

import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.command.arguments.EnumArgument;
import com.daqem.jobsplus.command.arguments.JobArgument;
import com.daqem.jobsplus.command.arguments.PowerupArgument;

import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.util.experience.ExperienceHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.stream.Collectors;

public class JobCommand {

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands
                .literal("job")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("debug")
                        .then(Commands.argument("target_player", EntityArgument.player())
                                .executes(context -> debug(context.getSource(), EntityArgument.getPlayer(context, "target_player")))
                        )
                        .executes(context -> debug(context.getSource(), context.getSource().getPlayer()))
                )
                .then(Commands.literal("set")
                        .then(Commands.literal("level")
                                .then(Commands.argument("target_player", EntityArgument.player())
                                        .then(Commands.argument("job", JobArgument.job())
                                                .then(Commands.argument("level", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                        .executes(context -> setLevel(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "target_player"),
                                                                JobArgument.getJob(context, "job"),
                                                                IntegerArgumentType.getInteger(context, "level"))
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("experience")
                                .then(Commands.argument("target_player", EntityArgument.player())
                                        .then(Commands.argument("job", JobArgument.job())
                                                .then(Commands.argument("experience", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                        .executes(context -> setExperience(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "target_player"),
                                                                JobArgument.getJob(context, "job"),
                                                                IntegerArgumentType.getInteger(context, "experience"))
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("coins")
                                .then(Commands.argument("target_player", EntityArgument.player())
                                        .then(Commands.argument("coins", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .executes(context -> setCoins(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "target_player"),
                                                        IntegerArgumentType.getInteger(context, "coins"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("powerup")
                                .then(Commands.argument("target_player", EntityArgument.player())
                                        .then(Commands.argument("job", JobArgument.job())
                                                .then(Commands.argument("powerup", PowerupArgument.powerup())
                                                        .then(Commands.argument("powerup_state", EnumArgument.enumArgument(PowerupState.class))
                                                                .executes(context -> setPowerup(
                                                                        context.getSource(),
                                                                        EntityArgument.getPlayer(context, "target_player"),
                                                                        JobArgument.getJob(context, "job"),
                                                                        PowerupArgument.getPowerup(context, "powerup"),
                                                                        context.getArgument("powerup_state", PowerupState.class)
                                                                ))
                                                        )
                                                )
                                                .then(Commands.literal("clear")
                                                        .executes(context -> clearPowerups(
                                                                context.getSource(),
                                                                EntityArgument.getPlayer(context, "target_player"),
                                                                JobArgument.getJob(context, "job")
                                                        ))
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("itemtag")
                        .executes(context -> {
                            ServerPlayer serverPlayer = context.getSource().getPlayer();
                            if (serverPlayer != null) {
                                serverPlayer.sendSystemMessage(JobsPlus.literal(
                                        serverPlayer.getMainHandItem().getTags().map(itemTagKey -> itemTagKey.location().toString()).collect(Collectors.joining(", "))
                                ));
                            }
                            return 0;
                        })
                )
                .then(Commands.literal("attributes")
                        .executes(context -> {
                            ServerPlayer serverPlayer = context.getSource().getPlayer();
                            if (serverPlayer != null) {
                                serverPlayer.getAttributes().getSyncableAttributes().forEach(attribute -> {
                                    serverPlayer.sendSystemMessage(JobsPlus.literal(
                                            attribute.getAttribute().getDescriptionId() + ": " + attribute.getValue()
                                    ));
                                    attribute.getModifiers().forEach(attributeModifier -> {
                                        serverPlayer.sendSystemMessage(JobsPlus.literal(
                                                attributeModifier.getName() + ": " + attributeModifier.getAmount()
                                        ));
                                    });
                                    serverPlayer.sendSystemMessage(JobsPlus.literal(" "));
                                });
                            }
                            return 0;
                        })
                )
        );
    }

    private static int clearPowerups(CommandSourceStack source, ServerPlayer targetPlayer, JobInstance jobInstance) {
        if (targetPlayer instanceof JobsServerPlayer jobsServerPlayer) {
            Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
            if (job != null) {
                job.getPowerupManager().clearPowerups();
                source.sendSuccess(JobsPlus.translatable(
                        "command.set.powerup.success_clear", jobInstance.getLocation()), false);
            }
        }
        return 0;
    }

    private static int debug(CommandSourceStack source, ServerPlayer target) {
        if (target instanceof ArcPlayer arcPlayer) {
            arcPlayer.arc$getActionHolders().forEach(actionHolder -> {
                source.sendSuccess(Component.literal(actionHolder.getLocation().toString()), false);
                source.sendSuccess(Component.literal("actions: " + actionHolder.getActions().size()), false);
                source.sendSuccess(Component.literal(" "), false);
            });
        }
        return 0;
    }

    private static int setPowerup(CommandSourceStack source, ServerPlayer target, JobInstance jobInstance, PowerupInstance powerupInstance, PowerupState powerupState) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
            job.getPowerupManager().forceAddPowerup(powerupInstance, powerupState);
            jobsServerPlayer.jobsplus$updateJob(job);
        }
        return 1;
    }

    private static int setCoins(CommandSourceStack source, ServerPlayer target, int coins) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            jobsServerPlayer.jobsplus$setCoins(coins);
            source.sendSuccess(JobsPlus.translatable(
                    "command.set.coins.success", coins, jobsServerPlayer.jobsplus$getName()), false);
        }
        return coins;
    }

    private static int setExperience(CommandSourceStack source, ServerPlayer target, JobInstance jobInstance, int experience) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
            if (job != null) {
                int maxExperienceForLevel = ExperienceHandler.getMaxExperienceForLevel(job.getLevel());
                if (experience >= maxExperienceForLevel) {
                    source.sendFailure(JobsPlus.translatable(
                            "command.set.experience.experience_too_high", maxExperienceForLevel));
                    return experience;
                } else if (job.getLevel() >= jobInstance.getMaxLevel()) {
                    source.sendFailure(JobsPlus.translatable(
                            "command.set.experience.already_max_level"));
                }
                job.setExperience(experience);
                source.sendSuccess(JobsPlus.translatable(
                        "command.set.experience.success", jobInstance.getLocation(), experience, jobsServerPlayer.jobsplus$getName()), false);
            } else {
                source.sendFailure(JobsPlus.translatable(
                        "command.does_not_have_job", jobsServerPlayer.jobsplus$getName(), jobInstance.getLocation()));
            }
        }
        return experience;
    }

    private static int setLevel(CommandSourceStack source, ServerPlayer target, JobInstance jobInstance, int level) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
            if (level == 0) {
                if (job != null) {
                    jobsServerPlayer.jobsplus$removeJob(jobInstance);
                    source.sendSuccess(JobsPlus.translatable(
                            "command.set.level.removed_job", jobInstance.getLocation(), jobsServerPlayer.jobsplus$getName()), false);
                } else {
                    source.sendFailure(JobsPlus.translatable(
                            "command.set.level.does_not_have_job"));
                }
                return level;
            } else if (level > jobInstance.getMaxLevel()) {
                source.sendFailure(JobsPlus.translatable(
                        "command.set.level.cannot_be_higher_than_max", jobInstance.getMaxLevel()));
                return level;
            }

            if (job != null) {
                job.setLevel(level);
                source.sendSuccess(JobsPlus.translatable(
                        "command.set.level.success", jobInstance.getLocation(), level, jobsServerPlayer.jobsplus$getName()), false);
            } else {
                job = jobsServerPlayer.jobsplus$addNewJob(jobInstance);
                if (job != null) {
                    job.setLevel(level);
                    source.sendSuccess(JobsPlus.translatable(
                            "command.set.level.success_new_job", jobInstance.getLocation(), level, jobsServerPlayer.jobsplus$getName()), false);
                } else {
                    source.sendFailure(JobsPlus.translatable(
                            "command.set.level.cannot_add_job"));
                }
            }
        } else {
            source.sendFailure(JobsPlus.translatable(
                    "command.set.level.invalid_target"));
        }
        return level;
    }
}
