package com.daqem.jobsplus.command.arguments;

import com.daqem.jobsplus.JobsPlus;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {
    private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType(
            (found, constants) -> JobsPlus.translatable("command.arguments.enum.invalid", constants, found));
    private final Class<T> enumClass;

    public static <R extends Enum<R>> EnumArgument<R> enumArgument(Class<R> enumClass) {
        return new EnumArgument<>(enumClass);
    }

    private EnumArgument(final Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T parse(final StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            throw INVALID_ENUM.createWithContext(reader, name, Arrays.toString(Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray()));
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Stream.of(enumClass.getEnumConstants()).map(Enum::name), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Stream.of(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }

    public static class Info<T extends Enum<T>> implements ArgumentTypeInfo<EnumArgument<T>, Info<T>.Template> {
        @Override
        public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
            buffer.writeUtf(template.enumClass.getName());
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {
            try {
                String name = buffer.readUtf();
                return new Template((Class<T>) Class.forName(name));
            } catch (ClassNotFoundException e) {
                //noinspection DataFlowIssue
                return null;
            }
        }

        @Override
        public void serializeToJson(Template template, JsonObject json) {
            json.addProperty("enum", template.enumClass.getName());
        }

        @Override
        public @NotNull Template unpack(EnumArgument<T> argument) {
            return new Template(argument.enumClass);
        }

        public class Template implements ArgumentTypeInfo.Template<EnumArgument<T>> {
            final Class<T> enumClass;

            Template(Class<T> enumClass) {
                this.enumClass = enumClass;
            }

            @Override
            public @NotNull EnumArgument<T> instantiate(@NotNull CommandBuildContext p_223435_) {
                return new EnumArgument<>(this.enumClass);
            }

            @Override
            public @NotNull ArgumentTypeInfo<EnumArgument<T>, ?> type() {
                return Info.this;
            }
        }
    }
}
