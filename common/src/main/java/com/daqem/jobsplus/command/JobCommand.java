package com.daqem.jobsplus.command;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.command.arguments.EnumArgument;
import com.daqem.jobsplus.command.arguments.JobArgument;
import com.daqem.jobsplus.command.arguments.PowerupArgument;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.daqem.jobsplus.util.experience.ExperienceHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

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
                                        )
                                )
                        )
                )
        );
    }

    private static int debug(CommandSourceStack source, ServerPlayer target) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            source.sendSuccess(JobsPlus.literal("Jobs: " + jobsServerPlayer.getJobs().stream().map(Job::toShortString).toList()), false);
        }
        return 0;
    }

    private static int setPowerup(CommandSourceStack source, ServerPlayer target, JobInstance jobInstance, PowerupInstance powerupInstance, PowerupState powerupState) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            if (jobsServerPlayer.hasJob(jobInstance)) {
                if (jobInstance.hasPowerup(powerupInstance)) {
                    if (powerupState == PowerupState.NOT_OWNED) {
                        jobsServerPlayer.removePowerup(powerupInstance);
                        source.sendSuccess(JobsPlus.translatable(
                                "command.set.powerup.success_remove", powerupInstance.getLocation(), jobInstance.getLocation()), false);
                        return 0;
                    }
                    jobsServerPlayer.setPowerup(powerupInstance, powerupState);
                    source.sendSuccess(JobsPlus.translatable(
                            "command.set.powerup.success", target.getName().getString(), powerupInstance.getLocation()), false);
                } else {
                    source.sendFailure(JobsPlus.translatable(
                            "command.set.powerup.not_available_for_job ", powerupInstance.getLocation(), jobInstance.getLocation()));
                }
            } else {
                source.sendFailure(JobsPlus.translatable(
                        "command.does_not_have_job", jobsServerPlayer.name(), jobInstance.getLocation()));
            }
        }
        return 0;
    }

    private static int setCoins(CommandSourceStack source, ServerPlayer target, int coins) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            jobsServerPlayer.setCoins(coins);
            source.sendSuccess(JobsPlus.translatable(
                    "command.set.coins.success", coins, jobsServerPlayer.name()), false);
        }
        return coins;
    }

    private static int setExperience(CommandSourceStack source, ServerPlayer target, JobInstance jobInstance, int experience) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {
            if (jobsServerPlayer.hasJob(jobInstance)) {
                Job job = jobsServerPlayer.getJob(jobInstance);
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
                        "command.set.experience.success", jobInstance.getLocation(), experience, jobsServerPlayer.name()), false);
            } else {
                source.sendFailure(JobsPlus.translatable(
                        "command.does_not_have_job", jobsServerPlayer.name(), jobInstance.getLocation()));
            }
        }
        return experience;
    }

    private static int setLevel(CommandSourceStack source, ServerPlayer target, JobInstance jobInstance, int level) {
        if (target instanceof JobsServerPlayer jobsServerPlayer) {

            if (level == 0) {
                if (jobsServerPlayer.hasJob(jobInstance)) {
                    jobsServerPlayer.removeJob(jobInstance);
                    source.sendSuccess(JobsPlus.translatable(
                            "command.set.level.removed_job", jobInstance.getLocation(), jobsServerPlayer.name()), false);
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

            if (jobsServerPlayer.hasJob(jobInstance)) {
                jobsServerPlayer.getJob(jobInstance).setLevel(level);
                source.sendSuccess(JobsPlus.translatable(
                        "command.set.level.success", jobInstance.getLocation(), level, jobsServerPlayer.name()), false);
            } else {
                jobsServerPlayer.addNewJob(jobInstance);
                jobsServerPlayer.getJob(jobInstance).setLevel(level);
                source.sendSuccess(JobsPlus.translatable(
                        "command.set.level.success_new_job", jobInstance.getLocation(), level, jobsServerPlayer.name()), false);
            }
        } else {
            source.sendFailure(JobsPlus.translatable(
                    "command.set.level.invalid_target"));
        }
        return level;
    }
}
