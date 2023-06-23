package com.daqem.jobsplus.resources.job.action;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.google.gson.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Action {

    private ResourceLocation location;
    private ResourceLocation jobLocation;
    private final ActionType type;
    private String shortDescription;
    private String description;
    private List<ActionReward> rewards = new ArrayList<>();
    private List<ActionCondition> conditions = new ArrayList<>();

    public Action(ActionType type) {
        this.type = type;
    }

    public Action withLocations(ResourceLocation location, ResourceLocation jobLocation) {
        this.location = location;
        this.jobLocation = jobLocation;
        return this;
    }

    public Action withDescriptions(String shortDescription, String description) {
        this.shortDescription = shortDescription;
        this.description = description;
        return this;
    }

    public Action withRewards(List<ActionReward> rewards) {
        this.rewards = rewards;
        return this;
    }

    public Action withConditions(List<ActionCondition> conditions) {
        this.conditions = conditions;
        return this;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public ResourceLocation getJobLocation() {
        return jobLocation;
    }

    public ActionType getType() {
        return type;
    }

    public Component getName() {
        return JobsPlus.translatable("action." + type.location().getPath());
    }

    public Component getDescription() {
        return JobsPlus.literal(this.description);
    }

    public Component getShortDescription() {
        return JobsPlus.literal(this.shortDescription);
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

    @Override
    public String toString() {
        return "Action{" +
                "location=" + location +
                ", jobLocation=" + jobLocation +
                ", type=" + type +
                ", shortDescription='" + shortDescription + '\'' +
                ", description='" + description + '\'' +
                ", rewards=" + rewards +
                ", conditions=" + conditions +
                '}';
    }

    public static class ActionSerializer<T extends Action> implements JsonDeserializer<T> {

        private final ResourceLocation location;

        public ActionSerializer(ResourceLocation location) {
            this.location = location;
        }

        private static Gson getGson() {
            GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
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

            String shortDescription = GsonHelper.getAsString(actionObject, "short_description", "");
            String description = GsonHelper.getAsString(actionObject, "description", "");

            ArrayList<ActionReward> actionRewardsList = new ArrayList<>();
            GsonHelper.getAsJsonArray(actionObject, "rewards")
                    .forEach(rewardElement ->
                            actionRewardsList.add(getGson().fromJson(rewardElement, ActionReward.class)));

            ArrayList<ActionCondition> actionConditionsList = new ArrayList<>();
            GsonHelper.getAsJsonArray(actionObject, "conditions", new JsonArray())
                    .forEach(condition ->
                            actionConditionsList.add(getGson().fromJson(condition.getAsJsonObject(), ActionCondition.class)));

            ResourceLocation location = new ResourceLocation(GsonHelper.getAsString(actionObject, "type"));
            Class<? extends Action> clazz = Actions.getClass(location);

            return (T) getGson().fromJson(actionObject, clazz)
                    .withLocations(this.location, new ResourceLocation(GsonHelper.getAsString(actionObject, "job")))
                    .withDescriptions(shortDescription, description)
                    .withRewards(actionRewardsList)
                    .withConditions(actionConditionsList);
        }
    }
}
