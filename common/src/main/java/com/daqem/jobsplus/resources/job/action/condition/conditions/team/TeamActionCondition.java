package com.daqem.jobsplus.resources.job.action.condition.conditions.team;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.world.scores.Team;

import java.lang.reflect.Type;

public class TeamActionCondition extends ActionCondition {

    private final String teamName;

    public TeamActionCondition(String teamName) {
        super(ActionConditions.TEAM);
        this.teamName = teamName;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        JobsServerPlayer player = actionData.getPlayer();
        Team team = player.getServerPlayer().getTeam();
        return team != null && team.getName().equals(teamName);
    }

    public static class Deserializer implements JsonDeserializer<TeamActionCondition> {

        @Override
        public TeamActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String teamName;
            if (jsonObject.has("team_name")) {
                teamName = jsonObject.get("team_name").getAsString();
            } else {
                throw new JsonParseException("Missing team_name, expected to find a string in TeamActionCondition");
            }
            return new TeamActionCondition(teamName);
        }
    }
}
