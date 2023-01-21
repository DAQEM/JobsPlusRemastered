package com.daqem.jobsplus.resources.job.action;

import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.type.BuildAction;
import com.daqem.jobsplus.resources.job.action.type.JobExpAction;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Action {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ActionReward.class, new ActionReward.ActionRewardSerializer<>())
            .registerTypeHierarchyAdapter(ActionCondition.class, new ActionCondition.ActionConditionSerializer<>())
            .registerTypeAdapter(BuildAction.class, new BuildAction.BuildActionSerializer())
            .registerTypeAdapter(JobExpAction.class, new JobExpAction.JobExpActionSerializer())
            .create();

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

    public List<ActionReward> getRewards() {
        return rewards;
    }

    public List<ActionCondition> getConditions() {
        return conditions;
    }

    public static class ActionSerializer<T extends Action> implements JsonDeserializer<T> {

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject actionObject = json.getAsJsonObject();

            ArrayList<ActionReward> tempRewards = new ArrayList<>();
            ArrayList<ActionCondition> tempConditions = new ArrayList<>();

            for (JsonElement rewards : actionObject.getAsJsonArray("rewards")) {
                JsonObject rewardObject = rewards.getAsJsonObject();
                ActionReward actionReward = GSON.fromJson(rewardObject, ActionReward.class);
                tempRewards.add(actionReward);
            }

            for (JsonElement rewards : actionObject.getAsJsonArray("conditions")) {
                JsonObject conditionObject = rewards.getAsJsonObject();
                ActionCondition actionCondition = GSON.fromJson(conditionObject, ActionCondition.class);
                tempConditions.add(actionCondition);
            }

            Class<? extends Action> clazz = Actions.getClass(new ResourceLocation(actionObject.get("type").getAsString()));
            actionObject.remove("type");
            return (T) GSON.fromJson(actionObject, clazz)
                    .withRewards(tempRewards)
                    .withConditions(tempConditions);
        }
    }
}
