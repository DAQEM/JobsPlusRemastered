package com.daqem.jobsplus;

import com.google.common.base.Suppliers;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class JobsPlus {
    public static final String MOD_ID = "jobsplus";
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    public static final CreativeModeTab TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "tab"), () ->
            new ItemStack(JobsPlus.ITEM.get()));
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Item> ITEM = ITEMS.register("item", () ->
            new Item(new Item.Properties().tab(JobsPlus.TAB)));
    
    public static void init() {
        ITEMS.register();
        
        System.out.println(JobsPlusExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
