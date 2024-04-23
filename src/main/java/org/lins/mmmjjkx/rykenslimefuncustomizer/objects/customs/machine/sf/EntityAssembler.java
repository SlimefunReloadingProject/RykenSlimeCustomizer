package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.AbstractEntityAssembler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityAssembler extends AbstractEntityAssembler<Entity> {
    private final EntityType type;
    private final int consumption;
    private final int capacity;
    private final ItemStack head;
    private final ItemStack body;

    public EntityAssembler(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                           @NotNull EntityType type, int consumption, int capacity, ItemStack head, ItemStack body) {
        super(itemGroup, item, recipeType, recipe);

        this.type = type;
        this.consumption = consumption;
        this.capacity = capacity;
        this.head = head.clone();
        this.body = body.clone();
    }

    @Override
    public int getEnergyConsumption() {
        return consumption;
    }

    @Override
    public ItemStack getHead() {
        return head;
    }

    @Override
    public ItemStack getBody() {
        return body;
    }

    @Override
    public Material getHeadBorder() {
        return Material.ORANGE_STAINED_GLASS_PANE;
    }

    @Override
    public Material getBodyBorder() {
        return Material.WHITE_STAINED_GLASS_PANE;
    }

    @Override
    public Entity spawnEntity(Location location) {
        return location.getWorld().spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
