package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import static org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine.RECIPE_INPUT;
import static org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine.RECIPE_OUTPUT;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.SingleItemRecipeGuideListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomTemplateCraftingOperation;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineTemplate;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ItemUtils;

public class CustomTemplateMachine extends AbstractEmptyMachine<CustomTemplateCraftingOperation>
        implements RecipeDisplayItem, EnergyNetComponent {
    private final MachineProcessor<CustomTemplateCraftingOperation> processor;

    @Getter
    private final CustomMenu menu;

    private final List<Integer> inputSlots;
    private final List<Integer> outputSlots;

    @Getter
    private final int templateSlot;

    @Getter
    private final List<MachineTemplate> templates;

    private final int consumption;
    private final int capacity;
    private final boolean fasterIfMoreTemplates;
    private final boolean moreOutputIfMoreTemplates;
    private final boolean hideAllRecipes;

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
            int capacity,
            boolean fasterIfMoreTemplates,
            boolean moreOutputIfMoreTemplates,
            boolean hideAllRecipes) {
        super(itemGroup, item, recipeType, recipe);

        this.processor = new MachineProcessor<>(this);
        this.menu = menu;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.templateSlot = templateSlot;
        this.templates = templates;
        this.consumption = consumption;
        this.capacity = capacity;
        this.fasterIfMoreTemplates = fasterIfMoreTemplates;
        this.moreOutputIfMoreTemplates = moreOutputIfMoreTemplates;
        this.hideAllRecipes = hideAllRecipes;

        createPreset(this, bmp -> {
            menu.apply(bmp);
            if (menu.getMenuClickHandler(templateSlot) != null) {
                bmp.addItem(templateSlot, null, ((player, i, itemStack, clickAction) -> true));
            }
        });

        this.addItemHandler(getBlockTicker());
        this.addItemHandler(new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(@NotNull Block block) {
                BlockMenu inv = StorageCacheUtils.getMenu(block.getLocation());
                if (inv != null) {
                    inv.dropItems(block.getLocation(), templateSlot);
                    inv.dropItems(block.getLocation(), getOutputSlots());
                    inv.dropItems(block.getLocation(), getInputSlots());
                }
            }
        });

        processor.setProgressBar(menu.getProgressBarItem());

        register(RykenSlimefunCustomizer.INSTANCE);
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

    @Override
    @NotNull public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        if (hideAllRecipes) {
            return displayRecipes;
        }

        int templateIndex = 0, recipeIndex = 0;
        for (MachineTemplate template : templates) {
            for (CustomMachineRecipe recipe : template.recipes()) {
                if (recipe.isHide()) {
                    continue;
                }

                if (recipe.getInput().length == 0) {
                    ItemStack templateItem = template.template().clone();
                    CommonUtils.addLore(templateItem, true, "&d&l&o*模板物品不消耗*");
                    displayRecipes.add(templateItem);
                } else {
                    displayRecipes.add(SingleItemRecipeGuideListener.tagItemTemplateRecipe(
                            RECIPE_INPUT, templateIndex, recipeIndex));
                }

                if (recipe.getOutput().length == 1) {
                    int seconds = recipe.getTicks() / 2;
                    ItemStack out = recipe.getOutput()[0].clone();
                    String rawLore = "&e制作时间: &b" + seconds + "&es";
                    if (seconds > 60) {
                        rawLore = rawLore.concat("(" + CommonUtils.formatSeconds(seconds) + "&e)");
                    }
                    CommonUtils.addLore(out, true, rawLore);

                    displayRecipes.add(out);
                } else {
                    displayRecipes.add(SingleItemRecipeGuideListener.tagItemTemplateRecipe(
                            RECIPE_OUTPUT, templateIndex, recipeIndex));
                }

                recipeIndex++;
            }
            templateIndex++;
            recipeIndex = 0;
        }

        return displayRecipes;
    }

    @SuppressWarnings("unused")
    private void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        BlockMenu inv = data.getBlockMenu();
        if (inv != null) {
            ItemStack templateItem = inv.getItemInSlot(templateSlot);

            // 断掉进度
            if (templateItem == null || templateItem.getType() == Material.AIR) {
                if (menu.getProgressSlot() >= 0) {
                    inv.replaceExistingItem(
                            menu.getProgressSlot(),
                            menu.getItems().getOrDefault(menu.getProgressSlot(), ChestMenuUtils.getBackground()));
                }
                processor.endOperation(b);
                return;
            }

            CustomTemplateCraftingOperation currentOperation = processor.getOperation(b);
            if (currentOperation != null) {
                if (!currentOperation.getTemplate().isItemSimilar(templateItem)) {
                    processor.endOperation(b);
                    if (menu.getProgressSlot() >= 0) {
                        inv.replaceExistingItem(
                                menu.getProgressSlot(),
                                menu.getItems().getOrDefault(menu.getProgressSlot(), ChestMenuUtils.getBackground()));
                    }
                    return;
                }

                if (this.takeCharge(b.getLocation())) {
                    if (!currentOperation.isFinished()) {
                        this.processor.updateProgressBar(inv, menu.getProgressSlot(), currentOperation);
                        currentOperation.addProgress(1);
                    } else {
                        if (menu.getProgressSlot() >= 0) {
                            inv.replaceExistingItem(
                                    menu.getProgressSlot(),
                                    menu.getItems()
                                            .getOrDefault(menu.getProgressSlot(), ChestMenuUtils.getBackground()));
                        }

                        List<ItemStack> result = currentOperation.getRecipe().getMatchChanceResult();

                        for (ItemStack output : result) {
                            ItemStack outputItem = output.clone();
                            if (moreOutputIfMoreTemplates) {
                                outputItem.setAmount(outputItem.getAmount() * templateItem.getAmount());
                            }
                            inv.pushItem(outputItem, this.getOutputSlots());
                        }

                        this.processor.endOperation(b);
                    }
                }
            } else {
                for (MachineTemplate template : templates) {
                    if (template.isItemSimilar(templateItem)) {
                        CustomMachineRecipe recipe = findNextRecipe(template, inv);
                        if (recipe != null) {
                            int ticks = recipe.getTicks();
                            if (fasterIfMoreTemplates) {
                                if (templateItem.getAmount() > 1) {
                                    ticks = ticks / templateItem.getAmount();
                                }
                            }
                            processor.startOperation(b, new CustomTemplateCraftingOperation(template, recipe, ticks));
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
    public MachineProcessor<CustomTemplateCraftingOperation> getMachineProcessor() {
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

    private CustomMachineRecipe findNextRecipe(MachineTemplate currentTemplate, BlockMenu inv) {
        List<CustomMachineRecipe> recipes = currentTemplate.recipes();

        Map<Integer, ItemStack> inventory = new HashMap<>();

        for (int slot : getInputSlots()) {
            ItemStack item = inv.getItemInSlot(slot);

            if (item != null) {
                inventory.put(slot, ItemStackWrapper.wrap(item));
            }
        }

        Map<CustomMachineRecipe, Map<Integer, Integer>> matched = new HashMap<>();

        for (CustomMachineRecipe recipe : recipes) {
            // Map<slot, amount>
            Map<Integer, Integer> found = new HashMap<>();

            if (recipe.getInput().length == 0) {
                if (getInputSlots().length == 0) {
                    if (!InvUtils.fitAll(inv.toInventory(), recipe.getOutput(), getOutputSlots())) {
                        return null;
                    }

                    return recipe;
                } else {
                    boolean allEmpty = true;
                    for (int i : getInputSlots()) {
                        ItemStack item = inv.getItemInSlot(i);
                        if (item != null && item.getType().isAir()) {
                            allEmpty = false;
                        }
                    }

                    if (allEmpty) {
                        return recipe;
                    }
                }
            }

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

        Map.Entry<CustomMachineRecipe, Map<Integer, Integer>> recipe = null;
        int max_Size = 0;

        for (Map.Entry<CustomMachineRecipe, Map<Integer, Integer>> item : matched.entrySet()) {
            int size = ItemUtils.getAllItemTypeAmount(item.getKey().getInput()) * 1000
                    + ItemUtils.getAllItemAmount(item.getKey().getInput());
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

        CustomMachineRecipe recipeKey = recipe.getKey();

        List<Map.Entry<Integer, Integer>> entries =
                new ArrayList<>(recipe.getValue().entrySet());
        IntList ints = recipeKey.getNoConsume();

        for (Map.Entry<Integer, Integer> entry : entries) {
            int i = entries.indexOf(entry);
            if (!ints.contains(i)) {
                inv.consumeItem(entry.getKey(), entry.getValue());
            }
        }

        return recipeKey;
    }

    @NotNull @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
