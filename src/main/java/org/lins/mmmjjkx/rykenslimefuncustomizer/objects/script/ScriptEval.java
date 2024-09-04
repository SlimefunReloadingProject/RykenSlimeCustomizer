package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.graalvm.polyglot.HostAccess;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
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

    protected final void setup() {
        addThing("server", Bukkit.getServer());

        // functions
        addThing("isPluginLoaded", (Function<String, Boolean>)
                s -> Bukkit.getPluginManager().isPluginEnabled(s));

        addThing("runOpCommand", (BiConsumer<Player, String>) (p, s) -> {
            p.setOp(true);
            p.performCommand(parsePlaceholder(p, s));
            p.setOp(false);
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
      
        addThing("runLater", (BiConsumer<Runnable, Long>) (r, l) -> Bukkit.getScheduler().runTaskLater(RykenSlimefunCustomizer.INSTANCE, r, l));
        addThing("runRepeating", (CiConsumer<Runnable, Long, Long>) (r, l, t) -> Bukkit.getScheduler().runTaskTimer(RykenSlimefunCustomizer.INSTANCE, r, l, t));
        addThing("runAsync", (Consumer<Runnable>) r -> Bukkit.getScheduler().runTaskAsynchronously(RykenSlimefunCustomizer.INSTANCE, r));
        addThing("runLaterAsync", (BiConsumer<Runnable, Long>) (r, l) -> Bukkit.getScheduler().runTaskLaterAsynchronously(RykenSlimefunCustomizer.INSTANCE, r, l));
        addThing("runRepeatingAsync", (CiConsumer<Runnable, Long, Long>) (r, l, t) -> Bukkit.getScheduler().runTaskTimerAsynchronously(RykenSlimefunCustomizer.INSTANCE, r, l, t));

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