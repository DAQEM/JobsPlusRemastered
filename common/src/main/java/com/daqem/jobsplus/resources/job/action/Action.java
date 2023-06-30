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
    private ResourceLocation forLocation;
    private ActionForType forType;
    private final ActionType type;
    private String shortDescription;
    private String description;
    private List<ActionReward> rewards = new ArrayList<>();
    private List<ActionCondition> conditions = new ArrayList<>();

    public Action(ActionType type) {
        this.type = type;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public Action withForLocationAndType(ResourceLocation forLocation, ActionForType forType) {
        this.forLocation = forLocation;
        this.forType = forType;
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

    public ResourceLocation getForLocation() {
        return forLocation;
    }

    public ActionForType getForType() {
        return forType;
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

    public boolean perform(ActionData actionData) {
        boolean shouldCancel = false;

        if (metConditions(actionData)) {
            JobsPlus.LOGGER.info("Action {} passed conditions for job {}", type.location(), actionData.getSourceJob().getJobInstance().getName());
            shouldCancel = applyRewards(actionData);
        }

        return shouldCancel;
    }

    public boolean metConditions(ActionData actionData) {
        return conditions.stream().allMatch(condition -> condition.isMet(actionData));
    }


    public boolean applyRewards(ActionData actionData) {
        boolean shouldCancel = false;

        for (ActionReward reward : rewards) {
            if (applyReward(actionData, reward)) shouldCancel = true;
        }

        return shouldCancel;
    }

    private static boolean applyReward(ActionData actionData, ActionReward reward) {
        if (reward.passedChance(actionData)) {
            return reward.apply(actionData);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Action{" +
                "location=" + location +
                ", jobLocation=" + forLocation +
                ", type=" + type +
                ", shortDescription='" + shortDescription + '\'' +
                ", description='" + description + '\'' +
                ", rewards=" + rewards +
                ", conditions=" + conditions +
                '}';
    }

    public static class ActionSerializer<T extends Action> implements JsonDeserializer<T> {


        private static final Gson GSON = createGson();

        private static Gson createGson() {
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

            ResourceLocation forLocation;
            ActionForType forType;
            if (!actionObject.has("powerup")) {
                forLocation = new ResourceLocation(GsonHelper.getAsString(actionObject, "job"));
                forType = ActionForType.JOB;
            } else {
                forLocation = new ResourceLocation(GsonHelper.getAsString(actionObject, "powerup"));
                forType = ActionForType.POWERUP;
            }


            String shortDescription = GsonHelper.getAsString(actionObject, "short_description", "");
            String description = GsonHelper.getAsString(actionObject, "description", "");

            ArrayList<ActionReward> actionRewardsList = new ArrayList<>();
            GsonHelper.getAsJsonArray(actionObject, "rewards")
                    .forEach(rewardElement ->
                            actionRewardsList.add(GSON.fromJson(rewardElement, ActionReward.class)));

            ArrayList<ActionCondition> actionConditionsList = new ArrayList<>();
            GsonHelper.getAsJsonArray(actionObject, "conditions", new JsonArray())
                    .forEach(condition ->
                            actionConditionsList.add(GSON.fromJson(condition.getAsJsonObject(), ActionCondition.class)));

            ResourceLocation location = new ResourceLocation(GsonHelper.getAsString(actionObject, "type"));
            Class<? extends Action> clazz = Actions.getClass(location);

            return (T) GSON.fromJson(actionObject, clazz)
                    .withForLocationAndType(forLocation, forType)
                    .withDescriptions(shortDescription, description)
                    .withRewards(actionRewardsList)
                    .withConditions(actionConditionsList);
        }
    }
}
