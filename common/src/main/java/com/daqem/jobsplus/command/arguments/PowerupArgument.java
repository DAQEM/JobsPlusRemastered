package com.daqem.jobsplus.command.arguments;


import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PowerupArgument implements ArgumentType<PowerupInstance> {

    public static PowerupArgument powerup() {
        return new PowerupArgument();
    }

    @Override
    public PowerupInstance parse(StringReader reader) throws CommandSyntaxException {
        return PowerupManager.getInstance().getAllPowerups().get(ResourceLocation.read(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<ResourceLocation> powerups = new ArrayList<>();
        try {
            powerups = context.getArgument("job", JobInstance.class).getPowerups().stream().map(PowerupInstance::getLocation).toList();
        } catch (NullPointerException ignored) {
        }
        return SharedSuggestionProvider.suggest(powerups.stream().map(ResourceLocation::toString), builder);
    }

    public static PowerupInstance getPowerup(CommandContext<?> context, String name) {
        return context.getArgument(name, PowerupInstance.class);
    }
}
