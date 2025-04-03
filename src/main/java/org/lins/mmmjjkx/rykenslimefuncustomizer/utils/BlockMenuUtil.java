package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.LinkedOutput;

@UtilityClass
public class BlockMenuUtil {
    @Nullable public static ItemStack pushItem(@Nonnull BlockMenu blockMenu, @Nonnull ItemStack item, int... slots) {
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalArgumentException("Cannot push null or AIR");
        }

        int leftAmount = item.getAmount();

        for (int slot : slots) {
            if (leftAmount <= 0) {
                break;
            }

            ItemStack existing = blockMenu.getItemInSlot(slot);

            if (existing == null || existing.getType() == Material.AIR) {
                int received = Math.min(leftAmount, item.getMaxStackSize());
                ItemStack clone = item.clone();
                clone.setAmount(received);
                blockMenu.replaceExistingItem(slot, clone);
                leftAmount -= received;
                item.setAmount(Math.max(0, leftAmount));
            } else {
                int existingAmount = existing.getAmount();
                if (existingAmount >= item.getMaxStackSize()) {
                    continue;
                }

                if (!StackUtils.itemsMatch(item, existing, true, false)) {
                    continue;
                }

                int received = Math.max(0, Math.min(item.getMaxStackSize() - existingAmount, leftAmount));
                leftAmount -= received;
                existing.setAmount(existingAmount + received);
                item.setAmount(leftAmount);
            }
        }

        if (leftAmount > 0) {
            return new CustomItemStack(item, leftAmount);
        } else {
            return null;
        }
    }

    @Nonnull
    public static Map<ItemStack, Integer> pushItem(
            @Nonnull BlockMenu blockMenu, @Nonnull ItemStack[] items, int... slots) {
        if (items == null || items.length == 0) {
            throw new IllegalArgumentException("Cannot push null or empty array");
        }

        List<ItemStack> listItems = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                listItems.add(item);
            }
        }

        return pushItem(blockMenu, listItems, slots);
    }

    @Nonnull
    public static Map<ItemStack, Integer> pushItem(
            @Nonnull BlockMenu blockMenu, @Nonnull List<ItemStack> items, int... slots) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot push null or empty list");
        }

        Map<ItemStack, Integer> itemMap = new HashMap<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                ItemStack leftOver = pushItem(blockMenu, item, slots);
                if (leftOver != null) {
                    itemMap.put(leftOver, itemMap.getOrDefault(leftOver, 0) + leftOver.getAmount());
                }
            }
        }

        return itemMap;
    }

    public static boolean fits(@Nonnull BlockMenu blockMenu, @Nonnull ItemStack item, int... slots) {
        if (item == null || item.getType() == Material.AIR) {
            return true;
        }

        int incoming = item.getAmount();
        for (int slot : slots) {
            ItemStack stack = blockMenu.getItemInSlot(slot);

            if (stack == null || stack.getType() == Material.AIR) {
                incoming -= item.getMaxStackSize();
            } else if (stack.getMaxStackSize() > stack.getAmount() && StackUtils.itemsMatch(item, stack, true, false)) {
                incoming -= stack.getMaxStackSize() - stack.getAmount();
            }

            if (incoming <= 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean fits(@Nonnull BlockMenu blockMenu, @Nonnull ItemStack[] items, int... slots) {
        if (items == null || items.length == 0) {
            return false;
        }

        List<ItemStack> listItems = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                listItems.add(item.clone());
            }
        }

        return fits(blockMenu, listItems, slots);
    }

    public static boolean fits(@Nonnull BlockMenu blockMenu, @Nonnull List<ItemStack> items, int... slots) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        List<ItemStack> cloneMenu = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            cloneMenu.add(null);
        }

        for (int slot : slots) {
            ItemStack stack = blockMenu.getItemInSlot(slot);
            if (stack != null && stack.getType() != Material.AIR) {
                cloneMenu.set(slot, stack.clone());
            } else {
                cloneMenu.set(slot, null);
            }
        }

        for (ItemStack rawItem : items) {
            ItemStack item = rawItem.clone();
            int leftAmount = item.getAmount();
            for (int slot : slots) {
                if (leftAmount <= 0) {
                    break;
                }

                ItemStack existing = cloneMenu.get(slot);

                if (existing == null || existing.getType() == Material.AIR) {
                    int received = Math.min(leftAmount, item.getMaxStackSize());
                    ItemStack clone = item.clone();
                    clone.setAmount(leftAmount);
                    cloneMenu.set(slot, clone);
                    leftAmount -= received;
                    item.setAmount(Math.max(0, leftAmount));
                } else {
                    int existingAmount = existing.getAmount();
                    if (existingAmount >= item.getMaxStackSize()) {
                        continue;
                    }

                    if (!StackUtils.itemsMatch(item, existing, true, false)) {
                        continue;
                    }

                    int received = Math.max(0, Math.min(item.getMaxStackSize() - existingAmount, leftAmount));
                    leftAmount -= received;
                    existing.setAmount(existingAmount + received);
                    item.setAmount(leftAmount);
                }
            }

            if (leftAmount > 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean fits(@Nonnull BlockMenu blockMenu, @Nonnull LinkedOutput output, int... slots) {
        if (output == null) {
            return false;
        }

        // clone the menu
        List<ItemStack> cloneMenu = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            cloneMenu.add(null);
        }

        for (int i = 0; i < 54; i++) {
            ItemStack stack = blockMenu.getItemInSlot(i);
            if (stack != null && stack.getType() != Material.AIR) {
                cloneMenu.set(i, stack.clone());
            } else {
                cloneMenu.set(i, null);
            }
        }

        // try push linked output
        for (int pushToSlot : output.getLinkedOutput().keySet()) {
            if (pushToSlot < 0 || pushToSlot >= 54) {
                continue;
            }

            ItemStack PitemToPush = output.getLinkedOutput().get(pushToSlot);
            if (PitemToPush == null || PitemToPush.getType() == Material.AIR) {
                continue;
            }

            ItemStack itemToPush = PitemToPush.clone();

            ItemStack existing = cloneMenu.get(pushToSlot);
            if (existing == null || existing.getType() == Material.AIR) {
                int received = Math.min(itemToPush.getAmount(), itemToPush.getMaxStackSize());
                if (received <= 0) {
                    return false;
                }
                ItemStack clone = itemToPush.clone();
                clone.setAmount(received);
                cloneMenu.set(pushToSlot, clone);
                itemToPush.setAmount(itemToPush.getAmount() - received);
            } else if (StackUtils.itemsMatch(itemToPush, existing, true, false)) {
                int existingAmount = existing.getAmount();
                int received = Math.min(itemToPush.getMaxStackSize() - existingAmount, itemToPush.getAmount());
                if (received <= 0) {
                    return false;
                }
                existing.setAmount(existingAmount + received);
                itemToPush.setAmount(itemToPush.getAmount() - received);
            } else {
                return false;
            }
        }

        // try to push free output
        for (ItemStack PitemToPush : output.getFreeOutput()) {
            if (PitemToPush == null || PitemToPush.getType() == Material.AIR) {
                continue;
            }

            ItemStack itemToPush = PitemToPush.clone();

            for (int slot : slots) {
                if (itemToPush.getAmount() <= 0) {
                    break;
                }

                ItemStack existing = cloneMenu.get(slot);
                if (existing == null || existing.getType() == Material.AIR) {
                    int received = Math.min(itemToPush.getAmount(), itemToPush.getMaxStackSize());
                    ItemStack clone = itemToPush.clone();
                    clone.setAmount(received);
                    cloneMenu.set(slot, clone);
                    itemToPush.setAmount(itemToPush.getAmount() - received);
                } else if (StackUtils.itemsMatch(itemToPush, existing, true, false)) {
                    int existingAmount = existing.getAmount();
                    int received = Math.min(itemToPush.getMaxStackSize() - existingAmount, itemToPush.getAmount());
                    existing.setAmount(existingAmount + received);
                    itemToPush.setAmount(itemToPush.getAmount() - received);
                }
            }
        }

        // all items should be pushed successfully
        return true;
    }

    public static void pushItem(
            @Nonnull BlockMenu blockMenu, @Nonnull LinkedOutput output, boolean chooseOneIfHas, int... slots) {
        if (output == null) {
            return;
        }

        // push linked output
        for (int pushToSlot : output.getLinkedOutput().keySet()) {
            if (pushToSlot < 0 || pushToSlot >= 54) {
                continue;
            }

            ItemStack PitemToPush = output.getLinkedOutput().get(pushToSlot);
            if (PitemToPush == null || PitemToPush.getType() == Material.AIR) {
                continue;
            }

            ItemStack itemToPush = PitemToPush.clone();

            int chance = output.getLinkedChances().get(pushToSlot);
            if (chance > 0 && chance < 100 && Math.random() * 100 > chance) {
                continue;
            }

            // ignore if not enough space
            pushItem(blockMenu, itemToPush, pushToSlot);

            if (chooseOneIfHas) {
                break;
            }
        }

        // push free output
        ItemStack[] freeOutput = output.getFreeOutput();
        for (int i = 0; i < freeOutput.length; i++) {
            ItemStack PitemToPush = freeOutput[i];
            if (PitemToPush == null || PitemToPush.getType() == Material.AIR) {
                continue;
            }

            ItemStack itemToPush = PitemToPush.clone();

            int chance = output.getFreeChances()[i];
            if (chance > 0 && chance < 100 && Math.random() * 100 > chance) {
                continue;
            }

            pushItem(blockMenu, itemToPush, slots);

            if (chooseOneIfHas) {
                break;
            }
        }
    }
}
