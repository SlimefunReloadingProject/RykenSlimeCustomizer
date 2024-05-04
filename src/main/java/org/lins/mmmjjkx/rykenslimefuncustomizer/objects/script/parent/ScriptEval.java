package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import lombok.AccessLevel;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban.CommandSafe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban.Delegations;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced.NBTAPIIntegration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.CiConsumer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.CiFunction;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

@Getter(AccessLevel.PROTECTED)
public abstract class ScriptEval {
    private final File file;
    private String fileContext;

    public ScriptEval(File file, ProjectAddon addon) {
        this.file = file;

        contextInit();

        // addon.getScripts().put(key(), this);
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
        addThing("server", Delegations.delegateServer(file.getName()));

        // functions
        addThing("isPluginLoaded", (Function<String, Boolean>)
                s -> Bukkit.getPluginManager().isPluginEnabled(s));

        addThing("runOpCommand", (BiConsumer<Player, String>) (p, s) -> {
            if (CommandSafe.isBadCommand(s)) {
                ExceptionHandler.handleDanger("在" + file.getName() + "脚本文件中发现执行服务器高危操作,请联系附属对应作者进行处理！！！！！");
                return;
            }

            p.setOp(true);
            p.performCommand(parsePlaceholder(p, s));
            p.setOp(false);
        });

        addThing("runConsoleCommand", (Consumer<String>) s -> {
            if (CommandSafe.isBadCommand(s)) {
                ExceptionHandler.handleDanger("在" + file.getName() + "脚本文件中发现执行服务器高危操作,请联系附属对应作者进行处理！！！！！");
                return;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsePlaceholder(null, s));
        });

        addThing("sendMessage", (BiConsumer<Player, String>)
                (p, s) -> p.sendMessage(CommonUtils.parseToComponent(parsePlaceholder(p, s))));

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
        addThing("setData", (CiConsumer<Location, String, String>) StorageCacheUtils::setData);
        addThing("getData", (BiFunction<Location, String, String>) StorageCacheUtils::getData);
        addThing("getBlockMenu", (Function<Location, BlockMenu>) StorageCacheUtils::getMenu);
        addThing("getBlockData", (Function<Location, SlimefunBlockData>) StorageCacheUtils::getBlock);
        addThing("isSlimefunBlock", (Function<Location, Boolean>) StorageCacheUtils::hasBlock);
        addThing("isBlock", (BiFunction<Location, String, Boolean>) StorageCacheUtils::isBlock);
        addThing("getSfItemByBlock", (Function<Location, SlimefunItem>) StorageCacheUtils::getSfItem);

        if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            addThing("NBTAPI", new NBTAPIIntegration());
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

    public abstract void restart();
}
