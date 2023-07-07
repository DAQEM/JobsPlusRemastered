package com.daqem.jobsplus.data.serializer;

import com.daqem.jobsplus.JobsPlus;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface JobsPlusSerializer<T> extends JsonDeserializer<T> {

    T fromNetwork(FriendlyByteBuf friendlyByteBuf);

    void toNetwork(FriendlyByteBuf friendlyByteBuf, T type);

    default ItemStack getItemStack(JsonObject jsonObject) {
        Item item = GsonHelper.getAsItem(jsonObject, "item");
        int count = GsonHelper.getAsInt(jsonObject, "count", 1);
        ItemStack iconStack = new ItemStack(item);

        iconStack.setCount(count);

        if (jsonObject.has("tag")) {
            String tagName = GsonHelper.getAsString(jsonObject, "tag");

            try {
                iconStack.setTag(TagParser.parseTag(tagName));
            } catch (CommandSyntaxException e) {
                String errorMessage = String.format("Error parsing tag for PowerupInstance icon %s: %s", tagName, e.getMessage());
                JobsPlus.LOGGER.error(errorMessage);
            }
        }

        return iconStack;
    }
}
