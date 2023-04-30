package com.daqem.jobsplus.resources.job.action;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.google.gson.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Action {

    private final ActionType type;
    private List<ActionReward> rewards = new ArrayList<>();
    private List<ActionCondition> conditions = new ArrayList<>();

    public Action(ActionType type) {
        this.type = type;
    }

    public Action withRewards(List<ActionReward> rewards) {
        this.rewards = rewards;
        return this;
    }

    public Action withConditions(List<ActionCondition> conditions) {
        this.conditions = conditions;
        return this;
    }

    public ActionType getType() {
        return type;
    }

    public Component getName() {
        return JobsPlus.translatable("action." + type.location().getPath());
    }

    public Component getDescription() {
        return JobsPlus.translatable("action." + type.location().getPath() + ".description");
    }

    public Component shortDescription() {
        return JobsPlus.translatable("action." + type.location().getPath() + ".short_description");
    }

    public List<ActionReward> getRewards() {
        return rewards;
    }

    public List<ActionCondition> getConditions() {
        return conditions;
    }

    public void perform(ActionData actionData) {
        if (metConditions(actionData)) {
            JobsPlus.LOGGER.info("Action {} passed conditions for job {}", type.location(), actionData.getSourceJob().getJobInstance().getName());
            applyRewards(actionData);
        }
    }

    public boolean metConditions(ActionData actionData) {
        for (ActionCondition condition : conditions) {
            if (!condition.isMet(actionData)) {
                return false;
            }
        }
        return true;
    }

    public void applyRewards(ActionData actionData) {
        for (ActionReward reward : rewards) {
            if (reward.passedChance(actionData)) {
                reward.apply(actionData);
            }
        }
    }

    public static class ActionSerializer<T extends Action> implements JsonDeserializer<T> {

        private static Gson getGson() {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeHierarchyAdapter(ActionReward.class, new ActionReward.ActionRewardSerializer<>())
                    .registerTypeHierarchyAdapter(ActionCondition.class, new ActionCondition.ActionConditionSerializer<>());

            for (ActionType type : Actions.ACTION_TYPES) {
                builder.registerTypeAdapter(type.clazz(), type.deserializer());
            }

            return builder.create();
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject actionObject = json.getAsJsonObject();

            ArrayList<ActionReward> tempRewards = new ArrayList<>();
            ArrayList<ActionCondition> tempConditions = new ArrayList<>();

            JsonArray rewards = actionObject.getAsJsonArray("rewards");
            if (rewards != null && !rewards.isEmpty()) {
                for (JsonElement reward : rewards) {
                    JsonObject rewardObject = reward.getAsJsonObject();
                    ActionReward actionReward = getGson().fromJson(rewardObject, ActionReward.class);
                    tempRewards.add(actionReward);
                }
            }

            JsonArray conditions = actionObject.getAsJsonArray("conditions");
            if (conditions != null && !conditions.isEmpty()) {
                for (JsonElement condition : conditions) {
                    JsonObject conditionObject = condition.getAsJsonObject();
                    ActionCondition actionCondition = getGson().fromJson(conditionObject, ActionCondition.class);
                    tempConditions.add(actionCondition);
                }
            }

            Class<? extends Action> clazz = Actions.getClass(new ResourceLocation(actionObject.get("type").getAsString()));
            actionObject.remove("type");
            return (T) getGson().fromJson(actionObject, clazz)
                    .withRewards(tempRewards)
                    .withConditions(tempConditions);
        }
    }
}
