package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Permission;
import java.security.Permissions;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.graalvm.polyglot.HostAccess;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced.NBTAPIIntegration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.CiConsumer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.CiFunction;

@Getter(AccessLevel.PROTECTED)
public abstract class ScriptEval {
    protected final HostAccess UNIVERSAL_HOST_ACCESS = HostAccess.newBuilder()
            .allowPublicAccess(true)
            .allowAllImplementations(true)
            .allowAllClassImplementations(true)
            .allowArrayAccess(true)
            .allowListAccess(true)
            .allowBufferAccess(true)
            .allowIterableAccess(true)
            .allowIteratorAccess(true)
            .allowMapAccess(true)
            .allowAccessInheritance(true)
            .targetTypeMapping(Double.class, Float.class, null, Double::floatValue)
            .targetTypeMapping(Integer.class, Float.class, null, Integer::floatValue)
            .targetTypeMapping(Boolean.class, String.class, null, String::valueOf)
            .targetTypeMapping(Integer.class, String.class, null, String::valueOf)
            .targetTypeMapping(Character.class, String.class, null, String::valueOf)
            .targetTypeMapping(Long.class, String.class, null, String::valueOf)
            .targetTypeMapping(Float.class, String.class, null, String::valueOf)
            .targetTypeMapping(Double.class, String.class, null, String::valueOf)
            .targetTypeMapping(Object.class, String.class, null, String::valueOf)
            .denyAccess(System.class)
            .denyAccess(Process.class)
            .denyAccess(Runtime.class)
            .denyAccess(ProcessBuilder.class)
            .denyAccess(ClassLoader.class)
            .denyAccess(Permission.class)
            .denyAccess(Permissions.class)
            .build();

    private final File file;
    private final ProjectAddon addon;
    private String fileContext;

    public ScriptEval(File file, ProjectAddon addon) {
        this.file = file;
        this.addon = addon;

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

    protected final void setup() {
        addThing("server", Bukkit.getServer());

        // functions
        addThing("isPluginLoaded", (Function<String, Boolean>)
                s -> Bukkit.getPluginManager().isPluginEnabled(s));

        addThing("runOpCommand", (BiConsumer<Player, String>) (p, s) -> {
            boolean op = p.isOp();
            p.setOp(true);
            try {
                p.performCommand(parsePlaceholder(p, s));
            } finally {
                p.setOp(op);
            }
        });

        addThing("runConsoleCommand", (Consumer<String>) s -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsePlaceholder(null, s));
        });

        addThing("sendMessage", (BiConsumer<Player, String>)
                (p, s) -> p.sendMessage(CMIChatColor.translate(parsePlaceholder(p, s))));

        // get slimefun item
        addThing("getSfItemById", (Function<String, SlimefunItem>) SlimefunItem::getById);
        addThing("getSfItemByItem", (Function<ItemStack, SlimefunItem>) SlimefunItem::getByItem);

        // SlimefunUtils functions
        addThing("isItemSimilar", (CiFunction<ItemStack, ItemStack, Boolean, Boolean>) SlimefunUtils::isItemSimilar);
        addThing("isRadioactiveItem", (Function<ItemStack, Boolean>) SlimefunUtils::isRadioactive);
        addThing("isSoulbound", (Function<ItemStack, Boolean>) SlimefunUtils::isSoulbound);
        addThing("canPlayerUseItem", (CiFunction<Player, ItemStack, Boolean, Boolean>) SlimefunUtils::canPlayerUseItem);

        // randint function
        addThing("randintA", (Function<Integer, Integer>) i -> new Random().nextInt(i));
        addThing("randintB", (BiFunction<Integer, Boolean, Integer>) (i, b) -> new Random().nextInt(b ? (i + 1) : i));
        addThing("randintC", (BiFunction<Integer, Integer, Integer>) (start, end) -> {
            IntStream is = IntStream.range(start, end);
            Random random = new Random();
            int[] arr = is.toArray();
            return arr[random.nextInt(arr.length)];
        });
        addThing("randintD", (CiFunction<Integer, Integer, Boolean, Integer>) (start, end, rangeClosed) -> {
            IntStream stream = rangeClosed ? IntStream.rangeClosed(start, end) : IntStream.range(start, end);
            Random random = new Random();
            int[] arr = stream.toArray();
            return arr[random.nextInt(arr.length)];
        });

        // StorageCacheUtils functions
        // removal
        addThing("setData", (CiConsumer<Location, String, String>) StorageCacheUtils::setData);
        addThing("getData", (BiFunction<Location, String, String>) StorageCacheUtils::getData);
        addThing("getBlockMenu", (Function<Location, BlockMenu>) StorageCacheUtils::getMenu);
        addThing("getBlockData", (Function<Location, SlimefunBlockData>) StorageCacheUtils::getBlock);
        addThing("isSlimefunBlock", (Function<Location, Boolean>) StorageCacheUtils::hasBlock);
        addThing("isBlock", (BiFunction<Location, String, Boolean>) StorageCacheUtils::isBlock);
        addThing("getSfItemByBlock", (Function<Location, SlimefunItem>) StorageCacheUtils::getSfItem);

        addThing("runLater", (BiFunction<Function<Object[], ?>, Integer, BukkitTask>) (r, l) -> {
            AtomicReference<BukkitTask> task = new AtomicReference<>();
            Bukkit.getScheduler().runTaskLater(RykenSlimefunCustomizer.INSTANCE, t -> {
                r.apply(new Object[]{t});
                task.set(t);
            }, l);
            return task.get();
        });
        addThing("runRepeating", (CiFunction<Function<Object[], ?>, Integer, Integer, BukkitTask>) (r, l, t) -> {
            AtomicReference<BukkitTask> task = new AtomicReference<>();
            Bukkit.getScheduler().runTaskTimer(RykenSlimefunCustomizer.INSTANCE, ta -> {
                r.apply(new Object[]{ta});
                task.set(ta);
            }, l, t);
            return task.get();
        });
        addThing("runAsync", (Function<Function<Object[], ?>, BukkitTask>) r -> {
            AtomicReference<BukkitTask> task = new AtomicReference<>();
            Bukkit.getScheduler().runTaskAsynchronously(RykenSlimefunCustomizer.INSTANCE, t -> {
                r.apply(new Object[]{t});
                task.set(t);
            });
            return task.get();
        });
        addThing("runLaterAsync", (BiFunction<Function<Object[], ?>, Integer, BukkitTask>) (r, l) -> {
            AtomicReference<BukkitTask> task = new AtomicReference<>();
            Bukkit.getScheduler().runTaskLaterAsynchronously(RykenSlimefunCustomizer.INSTANCE, t -> {
                r.apply(null);
                task.set(t);
            }, l);
            return task.get();
        });
        addThing("runRepeatingAsync", (CiFunction<Function<Object[], ?>, Integer, Integer, BukkitTask>) (r, l, t) -> {
            AtomicReference<BukkitTask> task = new AtomicReference<>();
            Bukkit.getScheduler().runTaskTimerAsynchronously(RykenSlimefunCustomizer.INSTANCE, ta -> {
                r.apply(null);
                task.set(ta);
            }, l, t);
            return task.get();
        });

        addThing("getAddonConfig", (Supplier<YamlConfiguration>) () -> {
            if (addon.getConfig() == null) {
                throw new RuntimeException("The addon does not have a config file!");
            }

            return addon.getConfig().config();
        });

        if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            addThing("NBTAPI", NBTAPIIntegration.instance);
        }
    }

    private String parsePlaceholder(@Nullable Player p, String text) {
        if (p != null) {
            text = text.replaceAll("%player%", p.getName());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(p, text);
        }

        return text;
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