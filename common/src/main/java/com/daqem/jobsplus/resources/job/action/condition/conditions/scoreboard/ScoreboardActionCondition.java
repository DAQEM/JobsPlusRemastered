package com.daqem.jobsplus.resources.job.action.condition.conditions.scoreboard;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

import java.lang.reflect.Type;

public class ScoreboardActionCondition extends ActionCondition {

    private final String objective;
    private final int minScore;
    private final int maxScore;

    public ScoreboardActionCondition(String objective, int minScore, int maxScore) {
        super(ActionConditions.SCOREBOARD);
        this.objective = objective;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Scoreboard scoreboard = actionData.getPlayer().getPlayer().getScoreboard();
        Objective objective = scoreboard.getObjective(this.objective);
        if (objective != null) {
            int score = scoreboard.getOrCreatePlayerScore(actionData.getPlayer().name(), objective).getScore();
            return score >= minScore && score <= maxScore;
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<ScoreboardActionCondition> {

        @Override
        public ScoreboardActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String objective;
            int minScore;
            int maxScore;
            if (jsonObject.has("objective")) {
                objective = jsonObject.get("objective").getAsString();
            } else {
                throw new JsonParseException("Missing objective, expected to find a Objective in ScoreboardActionCondition");
            }
            if (jsonObject.has("min_score")) {
                minScore = jsonObject.get("min_score").getAsInt();
            } else {
                throw new JsonParseException("Missing min_score, expected to find a int in ScoreboardActionCondition");
            }
            if (jsonObject.has("max_score")) {
                maxScore = jsonObject.get("max_score").getAsInt();
            } else {
                throw new JsonParseException("Missing max_score, expected to find a int in ScoreboardActionCondition");
            }
            return new ScoreboardActionCondition(objective, minScore, maxScore);
        }
    }
}
