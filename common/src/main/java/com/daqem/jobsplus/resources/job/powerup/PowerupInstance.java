package com.daqem.jobsplus.resources.job.powerup;

import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.Action;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PowerupInstance {

    private ResourceLocation location;
    private final String name;
    private final String description;
    private final int price;
    private final List<Action> actions;
    private @Nullable PowerupInstance parent;
    private final List<PowerupInstance> children;

    public PowerupInstance(String name, String description, int price, List<Action> actions, List<PowerupInstance> children) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.actions = actions;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public void setLocationFromJobLocation(ResourceLocation jobLocation) {
        setLocation(new ResourceLocation(jobLocation + "."
                + getName().toLowerCase()
                .replace(" ", "_")));
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }

    public List<PowerupInstance> getPowerups() {
        return children;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setParent(@Nullable PowerupInstance parent) {
        this.parent = parent;
    }

    public void setParents() {
        for (PowerupInstance powerupInstance : children) {
            powerupInstance.setParent(this);
            powerupInstance.setParents();
        }
    }

    @Override
    public String toString() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);
        json.addProperty("price", price);
        json.add("actions", GsonHelper.parseArray(new Gson().toJson(actions)));
        json.add("powerups", GsonHelper.parseArray(new Gson().toJson(children)));
        return json.toString();
    }


    public String toShortString() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("price", price);
        return json.toString();
    }


    @Nullable
    public static PowerupInstance of(ResourceLocation location) {
        return JobManager.getInstance().getPowerups().get(location);
    }

    public @Nullable PowerupInstance getParent() {
        return parent;
    }

    public List<PowerupInstance> getChildren() {
        return children;
    }

    public static class PowerupSerializer implements JsonDeserializer<PowerupInstance> {

        private static final Gson GSON = new GsonBuilder()
                .registerTypeHierarchyAdapter(Action.class, new Action.ActionSerializer<>())
                .registerTypeHierarchyAdapter(PowerupInstance.class, new PowerupInstance.PowerupSerializer())
                .create();

        @Override
        public PowerupInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            List<Action> actions = new ArrayList<>();
            GsonHelper.getAsJsonArray(jsonObject, "actions").forEach(jsonElement -> actions.add(GSON.fromJson(jsonElement, Action.class)));

            List<PowerupInstance> powerupInstances = new ArrayList<>();
            GsonHelper.getAsJsonArray(jsonObject, "powerups", new JsonArray()).forEach(jsonElement -> powerupInstances.add(GSON.fromJson(jsonElement, PowerupInstance.class)));


            return new PowerupInstance(
                    GsonHelper.getAsString(jsonObject, "name"),
                    GsonHelper.getAsString(jsonObject, "description"),
                    GsonHelper.getAsInt(jsonObject, "price"),
                    actions,
                    powerupInstances);
        }
    }
}
