package com.daqem.jobsplus.resources.job.powerup;

import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.Action;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PowerupInstance {

    private ResourceLocation location;
    private final String name;
    private final String description;
    private final boolean allJobs;
    private final List<ResourceLocation> jobs;
    private final int price;
    private final List<Action> actions;

    public PowerupInstance(String name, String description, boolean allJobs, List<ResourceLocation> jobs, int price, List<Action> actions) {
        this.name = name;
        this.description = description;
        this.allJobs = allJobs;
        this.jobs = jobs;
        this.price = price;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Powerup{" +
                "name='" + name +
                ", description='" + description +
                ", allJobs=" + allJobs +
                ", jobs=" + jobs +
                ", price=" + price +
                ", actions=" + actions +
                '}';
    }

    @Nullable
    public static PowerupInstance of(ResourceLocation location) {
        return JobManager.getInstance().getPowerups().get(location);
    }

    public static class PowerupSerializer implements JsonDeserializer<PowerupInstance> {

        private static final Gson GSON = new GsonBuilder()
                .registerTypeHierarchyAdapter(Action.class, new Action.ActionSerializer<>())
                .create();

        @Override
        public PowerupInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            List<ResourceLocation> jobs = new ArrayList<>();
            jsonObject.get("jobs").getAsJsonArray().forEach(jsonElement -> jobs.add(new ResourceLocation(jsonElement.getAsString())));
            List<Action> actions = new ArrayList<>();
            jsonObject.get("actions").getAsJsonArray().forEach(jsonElement -> actions.add(GSON.fromJson(jsonElement, Action.class)));

            return new PowerupInstance(
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("all_jobs").getAsBoolean(),
                    jobs,
                    jsonObject.get("price").getAsInt(),
                    actions);
        }
    }
}
