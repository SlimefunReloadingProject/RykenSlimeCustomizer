package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import java.util.List;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;

@SuppressWarnings("deprecation")
public class ScriptedEvalBreakHandler extends BlockBreakHandler {
    private final ScriptEval eval;
    private final InventoryBlock machine;

    public ScriptedEvalBreakHandler(InventoryBlock machine, ScriptEval eval) {
        super(false, false);

        this.eval = eval;
        this.machine = machine;
    }

    @Override
    public void onPlayerBreak(
            BlockBreakEvent blockBreakEvent, @NotNull ItemStack itemStack, @NotNull List<ItemStack> list) {
        Block block = blockBreakEvent.getBlock();
        Location loc = block.getLocation();
        BlockMenu bm = BlockStorage.getInventory(loc);
        if (bm != null) {
            if (machine.getInputSlots().length > 0) {
                bm.dropItems(loc, machine.getInputSlots());
            }
            if (machine.getOutputSlots().length > 0) {
                bm.dropItems(loc, machine.getOutputSlots());
            }
        }

        if (eval != null) {
            eval.evalFunction("onBreak", blockBreakEvent, itemStack, list);
        }
    }
}
