package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.SingleItemRecipeGuideListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomLinkedMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.BlockMenuUtil;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.StackUtils;

public class CustomWorkbench extends AContainer implements EnergyNetComponent, RecipeDisplayItem {
    private final BlockTicker blockTicker = new BlockTicker() {
        @Override
        public boolean isSynchronized() {
            return false;
        }

        @Override
        public void tick(Block b, SlimefunItem slimefunItem, SlimefunBlockData data) {}
    };
    private final MachineProcessor<CraftingOperation> processor;
    private final int[] input;
    private final int[] output;
    private final List<CustomLinkedMachineRecipe> raw_recipes;
    private final List<CustomLinkedMachineRecipe> recipes;
    private final int energyPerCraft;
    private final int capacity;
    private final boolean hideAllRecipes;
    private final int click;
    private final ScriptEval eval;

    public static final ItemStack RECIPE_INPUT =
            new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输入", "", "&2> &a点击查看");
    public static final ItemStack RECIPE_OUTPUT =
            new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输出", "", "&2> &a点击查看");

    @Getter
    @Nullable private final CustomMenu menu;

    public CustomWorkbench(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int[] input,
            int[] output,
            List<CustomLinkedMachineRecipe> recipes,
            int energyPerCraft,
            int capacity,
            @Nullable CustomMenu menu,
            boolean hideAllRecipes,
            int click,
            @Nullable ScriptEval eval) {
        super(itemGroup, item, recipeType, recipe);

        this.processor = new MachineProcessor<>(this);
        this.input = input;
        this.output = output;
        this.raw_recipes = recipes;
        this.recipes = new ArrayList<>(
                raw_recipes.stream().filter(r -> !r.isForDisplay()).toList());
        this.energyPerCraft = energyPerCraft;
        this.capacity = capacity;
        this.menu = menu;
        this.hideAllRecipes = hideAllRecipes;
        this.click = click;
        this.eval = eval;

        if (menu == null) {
            ExceptionHandler.handleError("未找到菜单 " + item.getItemId());
            return;
        }

        this.processor.setProgressBar(menu.getProgressBarItem());

        if (eval != null) {
            eval.doInit();
        }

        new BlockMenuPreset(this.getId(), this.getItemName()) {
            public void init() {
                menu.apply(this);
            }

            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                menu.addMenuClickHandler(
                        CustomWorkbench.this.click, (player, clickedSlot, clickedItem, clickAction) -> {
                            if (CustomWorkbench.this.eval != null) {
                                Object result = CustomWorkbench.this.eval.evalFunction(
                                        "onClick", this, player, clickedSlot, clickedItem, clickAction);
                                if (result instanceof Boolean booleanResult) {
                                    return booleanResult;
                                }

                                return false;
                            } else {
                                if (!takeCharge(menu.getLocation())) {
                                    return false;
                                }

                                CustomLinkedMachineRecipe nextRecipe = CustomWorkbench.this.findNextLinkedRecipe(menu);
                                if (nextRecipe != null) {
                                    BlockMenuUtil.pushItem(
                                            menu, nextRecipe.getLinkedOutput(), nextRecipe.isChooseOneIfHas());
                                }

                                return false;
                            }
                        });
            }

            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return flow == ItemTransportFlow.INSERT ? input : output;
            }

            public boolean canOpen(Block b, Player p) {
                if (p.hasPermission("slimefun.inventory.bypass")) {
                    return true;
                } else {
                    return CustomWorkbench.this.canUse(p, false)
                            && Slimefun.getProtectionManager()
                                    .hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
                }
            }
        };

        setProcessingSpeed(1);
        setCapacity(capacity);
        setEnergyConsumption(energyPerCraft);

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @NotNull @Override
    protected BlockBreakHandler onBlockBreak() {
        return new SimpleBlockBreakHandler() {
            public void onBlockBreak(@NotNull Block b) {
                BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());
                if (inv != null) {
                    inv.dropItems(b.getLocation(), CustomWorkbench.this.getInputSlots());
                    inv.dropItems(b.getLocation(), CustomWorkbench.this.getOutputSlots());
                }

                CustomWorkbench.this.processor.endOperation(b);
            }
        };
    }

    @Override
    protected void registerDefaultRecipes() {
        if (recipes == null || recipes.isEmpty()) {
            return;
        }

        recipes.forEach(super::registerRecipe);
    }

    @Override
    public int getEnergyConsumption() {
        return energyPerCraft;
    }

    @Override
    public @NotNull MachineProcessor<CraftingOperation> getMachineProcessor() {
        return this.processor;
    }

    @Override
    // Outside init
    public ItemStack getProgressBar() {
        return new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    }

    @Override
    @NotNull public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        if (hideAllRecipes) {
            return displayRecipes;
        }

        int i = 0;
        for (CustomLinkedMachineRecipe recipe : raw_recipes) {
            if (recipe.isHide()) {
                continue;
            }

            ItemStack[] input = recipe.getInput();
            ItemStack[] output = recipe.getOutput();

            if (input.length == 1) {
                displayRecipes.add(input[0]);
            } else {
                ItemStack in = SingleItemRecipeGuideListener.tagItemWorkbenchRecipe(RECIPE_INPUT, i);
                displayRecipes.add(in);
            }

            if (output.length == 1) {
                displayRecipes.add(output[0]);
            } else {
                ItemStack out = SingleItemRecipeGuideListener.tagItemWorkbenchRecipe(RECIPE_OUTPUT, i);
                displayRecipes.add(out);
            }

            i++;
        }

        return displayRecipes;
    }

    @Override
    public int[] getInputSlots() {
        return input;
    }

    @Override
    public int[] getOutputSlots() {
        return output;
    }

    @NotNull @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @NotNull @Override
    public String getMachineIdentifier() {
        return getId();
    }

    @Override
    protected void constructMenu(BlockMenuPreset preset) {}

    @Override
    protected void tick(Block b) {}

    @Nullable public CustomLinkedMachineRecipe findNextLinkedRecipe(BlockMenu blockMenu) {
        for (CustomLinkedMachineRecipe recipe : this.recipes) {
            Map<Integer, ItemStack> inputMap = recipe.getLinkedInput();
            boolean matched = true;
            for (int slot : inputMap.keySet()) {
                ItemStack item = blockMenu.getItemInSlot(slot);

                if (!StackUtils.itemsMatch(item, inputMap.get(slot), false, true)) {
                    matched = false;
                    break;
                }
            }
            if (!matched) {
                continue;
            }

            if (!BlockMenuUtil.fits(blockMenu, recipe.getLinkedOutput())) {
                continue;
            }

            for (int slot : inputMap.keySet()) {
                if (recipe.getNoConsumes().contains(slot)) {
                    continue;
                }
                ItemStack itemStack = blockMenu.getItemInSlot(slot);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    blockMenu.consumeItem(slot, inputMap.get(slot).getAmount());
                }
            }
            return recipe;
        }
        return null;
    }

    @Override
    public BlockTicker getBlockTicker() {
        return blockTicker;
    }
}
