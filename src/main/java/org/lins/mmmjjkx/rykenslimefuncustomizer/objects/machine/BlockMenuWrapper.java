package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import lombok.RequiredArgsConstructor;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BlockMenuWrapper {
    private final BlockMenu blockMenu;

    public ItemStack getItemInSlot(int slot) {
        return blockMenu.getItemInSlot(slot);
    }

    public void replaceExistingItem(int slot, ItemStack item) {
        blockMenu.replaceExistingItem(slot, item);
    }

    public void addItem(int slot, ItemStack item) {
        blockMenu.addItem(slot, item);
    }

    public ItemStack[] getContents() {
        return blockMenu.getContents();
    }

    public boolean fits(@NotNull ItemStack item, int... slots) {
        return blockMenu.fits(item, slots);
    }

    @Nullable
    public ItemStack pushItem(@NotNull ItemStack item, int... slots) {
        return blockMenu.pushItem(item, slots);
    }

    public void consumeItem(int slot) {
        this.consumeItem(slot, 1);
    }

    public void consumeItem(int slot, int amount) {
        this.consumeItem(slot, amount, false);
    }

    public void consumeItem(int slot, int amount, boolean replaceConsumables) {
        blockMenu.consumeItem(slot, amount, replaceConsumables);
    }

    public void reload() {
        blockMenu.reload();
    }

    public Inventory getInventory() {
        return blockMenu.getInventory();
    }

    public List<ItemStack> getItemInSlots(int... slots) {
        List<ItemStack> items = new ArrayList<>();
        for (int slot : slots) {
            items.add(getItemInSlot(slot));
        }
        return items;
    }
}
