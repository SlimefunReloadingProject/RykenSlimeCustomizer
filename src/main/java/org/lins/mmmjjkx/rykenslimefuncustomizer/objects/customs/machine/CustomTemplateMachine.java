package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;

import java.util.*;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomCraftingOperation;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomTemplateMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineTemplate;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ItemUtils;

public class CustomTemplateMachine extends AbstractEmptyMachine<CraftingOperation> implements RecipeDisplayItem, EnergyNetComponent {
    private final MachineProcessor<CraftingOperation> processor;

    @Getter
    private final CustomMenu menu;

    private final List<Integer> inputSlots;
    private final List<Integer> outputSlots;
    private final int templateSlot;
    private final List<MachineTemplate> templates;
    private final int consumption;
    private final int capacity;

    public CustomTemplateMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @NotNull CustomMenu menu,
            List<Integer> inputSlots,
            List<Integer> outputSlots,
            int templateSlot,
            List<MachineTemplate> templates,
            int consumption,
            int capacity) {
        super(itemGroup, item, recipeType, recipe);

        this.processor = new MachineProcessor<>(this);
        this.menu = menu;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.templateSlot = templateSlot;
        this.templates = templates;
        this.consumption = consumption;
        this.capacity = capacity;

        createPreset(this, bmp -> {
            menu.apply(bmp);
            if (menu.getMenuClickHandler(templateSlot) != null) {
                bmp.addItem(templateSlot, null, ((player, i, itemStack, clickAction) -> true));
            }
        });
    }

    @Override
    public BlockTicker getBlockTicker() {
        return new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return true;
            }

            @Override
            public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                CustomTemplateMachine.this.tick(b, item, data);
            }
        };
    }

    private void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        BlockMenu inv = data.getBlockMenu();
        if (inv != null) {
            ItemStack templateItem = inv.getItemInSlot(templateSlot);
            CustomCraftingOperation currentOperation = (CustomCraftingOperation) processor.getOperation(b);
            if (currentOperation != null) {
                if (this.takeCharge(b.getLocation())) {
                    if (!currentOperation.isFinished()) {
                        this.processor.updateProgressBar(inv, 22, currentOperation);
                        currentOperation.addProgress(1);
                    } else {
                        inv.replaceExistingItem(22, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " ", new String[0]));
                        List<ItemStack> result = currentOperation.getRecipe().getMatchChanceResult();

                        for (ItemStack output : result) {
                            inv.pushItem(output.clone(), this.getOutputSlots());
                        }

                        this.processor.endOperation(b);
                    }
                }
            } else {
                for (MachineTemplate template : templates) {
                    if (template.isItemSimilar(templateItem)) {
                        CustomTemplateMachineRecipe recipe = findNextRecipe(template, inv);
                        if (recipe != null) {
                            processor.startOperation(b, new CustomCraftingOperation(recipe));
                        }
                    }
                }
            }
        }
    }

    private boolean takeCharge(Location l) {
        Validate.notNull(l, "Can't attempt to take charge from a null location!");
        if (this.isChargeable()) {
            int charge = this.getCharge(l);
            if (charge < consumption) {
                return false;
            } else {
                this.setCharge(l, charge - consumption);
                return true;
            }
        } else {
            return true;
        }
    }

    @NotNull @Override
    public MachineProcessor<CraftingOperation> getMachineProcessor() {
        return processor;
    }

    @Override
    public int[] getInputSlots() {
        return inputSlots.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public int[] getOutputSlots() {
        return outputSlots.stream().mapToInt(i -> i).toArray();
    }

    @NotNull @Override
    public List<ItemStack> getDisplayRecipes() {
        return List.of();
    }

    private CustomTemplateMachineRecipe findNextRecipe(MachineTemplate currentTemplate, BlockMenu inv) {
        List<CustomTemplateMachineRecipe> recipes = currentTemplate.recipes();

        Map<Integer, ItemStack> inventory = new HashMap<>();

        for (int slot : getInputSlots()) {
            ItemStack item = inv.getItemInSlot(slot);

            if (item != null) {
                inventory.put(slot, ItemStackWrapper.wrap(item));
            }
        }

        Map<CustomTemplateMachineRecipe, Map<Integer, Integer>> matched = new HashMap<>();

        for (CustomTemplateMachineRecipe recipe : recipes) {
            //Map<slot, amount>
            Map<Integer, Integer> found = new HashMap<>();

            for (ItemStack input : recipe.getInput()) {
                for (int slot : getInputSlots()) {
                    if (found.containsKey(slot)) {
                        continue;
                    }

                    if (SlimefunUtils.isItemSimilar(inventory.get(slot), input, true)) {
                        found.put(slot, input.getAmount());
                        break;
                    }
                }

                if (found.size() == recipe.getInput().length) {
                    matched.put(recipe, found);
                }
            }
        }

        if (matched.isEmpty()) {
            return null;
        }

        Map.Entry<CustomTemplateMachineRecipe, Map<Integer, Integer>> recipe = null;
        int max_Size = 0;

        for (Map.Entry<CustomTemplateMachineRecipe, Map<Integer, Integer>> item : matched.entrySet()) {
            int size = ItemUtils.getAllItemTypeAmount(item.getKey().getInput()) * 1000 + ItemUtils.getAllItemAmount(item.getKey().getInput());
            if (size > max_Size) {
                recipe = item;
                max_Size = size;
            }
        }

        if (recipe == null) {
            return null;
        }

        if (!InvUtils.fitAll(inv.toInventory(), recipe.getKey().getOutput(), getOutputSlots())) {
            return null;
        }

        int cost = recipe.getKey().getCost();

        int templateAmount = inv.getItemInSlot(templateSlot).getAmount();

        if (templateAmount < cost) {
            return null;
        } else {
            int take = templateAmount - cost;
            inv.consumeItem(templateSlot, take);
        }

        for (Map.Entry<Integer, Integer> entry : recipe.getValue().entrySet()) {
            inv.consumeItem(entry.getKey(), entry.getValue());
        }

        return recipe.getKey();
    }

    @NotNull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
