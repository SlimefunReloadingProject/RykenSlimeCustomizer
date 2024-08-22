package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced.NBTAPIIntegration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.CiConsumer;

@Getter(AccessLevel.PROTECTED)
public abstract class ScriptEval {
    private final File file;
    private String fileContext;

    public ScriptEval(File file) {
        this.file = file;

        contextInit();
    }

    public abstract String key();

    protected void contextInit() {
        try {
            fileContext = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            fileContext = "";
            e.printStackTrace();
        } catch (IOException e) {
            fileContext = "";
            e.printStackTrace();
        }
    }

    @Deprecated
    protected final void setup() {
        // StorageCacheUtils functions
        // removal
        addThing("setData", (CiConsumer<Location, String, String>) StorageCacheUtils::setData);
        addThing("getData", (BiFunction<Location, String, String>) StorageCacheUtils::getData);
        addThing("getBlockMenu", (Function<Location, BlockMenu>) StorageCacheUtils::getMenu);
        addThing("getBlockData", (Function<Location, SlimefunBlockData>) StorageCacheUtils::getBlock);
        addThing("isSlimefunBlock", (Function<Location, Boolean>) StorageCacheUtils::hasBlock);
        addThing("isBlock", (BiFunction<Location, String, Boolean>) StorageCacheUtils::isBlock);
        addThing("getSfItemByBlock", (Function<Location, SlimefunItem>) StorageCacheUtils::getSfItem);

        // bukkit scheduler functions
        addThing("runLater", (BiConsumer<Runnable, Long>) (r, l) -> Bukkit.getScheduler().runTaskLater(RykenSlimefunCustomizer.INSTANCE, r, l));
        addThing("runRepeating", (CiConsumer<Runnable, Long, Long>) (r, l, t) -> Bukkit.getScheduler().runTaskTimer(RykenSlimefunCustomizer.INSTANCE, r, l, t));
        addThing("runAsync", (Consumer<Runnable>) r -> Bukkit.getScheduler().runTaskAsynchronously(RykenSlimefunCustomizer.INSTANCE, r));
        addThing("runLaterAsync", (BiConsumer<Runnable, Long>) (r, l) -> Bukkit.getScheduler().runTaskLaterAsynchronously(RykenSlimefunCustomizer.INSTANCE, r, l));
        addThing("runRepeatingAsync", (CiConsumer<Runnable, Long, Long>) (r, l, t) -> Bukkit.getScheduler().runTaskTimerAsynchronously(RykenSlimefunCustomizer.INSTANCE, r, l, t));

        // NBTAPI integration
        if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            addThing("NBTAPI", NBTAPIIntegration.instance);
        }
    }

    public abstract void addThing(String name, Object value);

    public final void doInit() {
        if (fileContext == null || fileContext.isBlank()) {
            contextInit();
        }

        evalFunction("init");
    }

    @CanIgnoreReturnValue
    @Nullable public abstract Object evalFunction(String functionName, Object... args);

    public abstract void close();
}
