package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import java.util.List;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineTemplate;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.RecipeMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

public class CustomTemplateMachine extends AbstractEmptyMachine<CraftingOperation> implements RecipeDisplayItem {
    private final MachineProcessor<CraftingOperation> processor;

    @Getter
    private final CustomMenu menu;

    private final List<Integer> inputSlots;
    private final List<Integer> outputSlots;
    private final int templateSlot;
    private final List<MachineTemplate> templates;

    public CustomTemplateMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @NotNull CustomMenu menu,
            List<Integer> inputSlots,
            List<Integer> outputSlots,
            int templateSlot,
            List<MachineTemplate> templates) {
        super(itemGroup, item, recipeType, recipe);

        this.processor = new MachineProcessor<>(this);
        this.menu = menu;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.templateSlot = templateSlot;
        this.templates = templates;

        if (menu.getMenuClickHandler(templateSlot) != null) {
            menu.addItem(templateSlot, null, ((player, i, itemStack, clickAction) -> true));
        }
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

    protected void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        BlockMenu m = data.getBlockMenu();
        if (m != null) {
            ItemStack templateItem = m.getItemInSlot(templateSlot);
            MachineTemplate template = CommonUtils.getIf(templates, t -> t.isSimilar(templateItem));
            if (template != null) {}
        }
    }

    private RecipeMachineRecipe findRecipe(MachineTemplate currentTemplate, BlockMenu menu) {
        return null;
    }

    @NotNull @Override
    public List<ItemStack> getDisplayRecipes() {
        return List.of();
    }
}
