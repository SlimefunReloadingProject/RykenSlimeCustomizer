package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineRecord;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class CustomEnergyGenerator extends CustomMachine implements EnergyNetProvider {
    private final ScriptEval eval;
    private final int defaultOutput;

    public CustomEnergyGenerator(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @Nullable CustomMenu menu,
            List<Integer> input,
            List<Integer> output,
            MachineRecord record,
            EnergyNetComponentType type,
            @Nullable ScriptEval eval,
            int defaultOutput) {
        super(itemGroup, item, recipeType, recipe, menu, input, output, record, type, eval);

        this.eval = eval;
        this.defaultOutput = defaultOutput;
    }

    @Override
    public int getGeneratedOutput(@NotNull Location l, @NotNull SlimefunBlockData data) {
        if (eval == null) {
            return defaultOutput;
        } else {
            try {
                Object result = eval.evalFunction("getGeneratedOutput", l, data);
                if (result instanceof Integer i) {
                    return i;
                } else {
                    ExceptionHandler.handleWarning(
                            "getGeneratedOutput() 返回了一个非整数值: " + result + " 导致自定义发电机的默认输出值将被使用， 请找附属对应作者修复此问题！");
                    return defaultOutput;
                }
            } catch (Exception e) {
                return defaultOutput;
            }
        }
    }
}
