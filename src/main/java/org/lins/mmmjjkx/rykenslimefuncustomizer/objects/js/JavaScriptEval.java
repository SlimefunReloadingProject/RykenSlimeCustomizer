package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.clip.placeholderapi.PlaceholderAPI;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.ban.Delegations;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.lambda.CiConsumer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.lambda.CiFunction;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.FileHandler;
import java.util.stream.IntStream;

public class JavaScriptEval {
    private final GraalJSScriptEngine jsEngine;
    private final File js;
    private String context;
    private final FileHandler log;

    public JavaScriptEval(@NotNull File js, ProjectAddon addon) {
        this.js = js;
        log = createLogFileHandler(addon);

        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        System.setProperty("polyglot.js.nashorn-compat", "true");

        jsEngine = GraalJSScriptEngine.create(null, Context.newBuilder("js")
                .allowAllAccess(true)
                .allowHostClassLookup(s -> true)
                .allowHostAccess(HostAccess.ALL)
                .allowNativeAccess(false)
                .allowExperimentalOptions(true)
                .logHandler(log)
        );
        setup();
        contextInit();

        addon.getScriptEvals().add(this);
    }

    private FileHandler createLogFileHandler(ProjectAddon addon) {
        File dir = new File(addon.getScriptsFolder(), "logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, js.getName()+"-%g.log");

        try {
            return new FileHandler(dest.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            jsEngine.close();
            log.close();
        } catch (IllegalStateException ignored) {}
    }

    public void addThing(String name, Object value) {
        jsEngine.put(name, value);
    }

    private void contextInit() {
        try {
            context = Files.readString(js.toPath(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            try {
                js.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            context = "";
            e.printStackTrace();
        } catch (IOException e) {
            context = "";
            e.printStackTrace();
        }

        try {
            jsEngine.eval(context);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    private void setup() {
        jsEngine.put("server", Delegations.delegateServer(js.getName()));
        jsEngine.put("sfPlugin", Slimefun.getPlugin(Slimefun.class));

        //functions
        jsEngine.put("isPluginLoaded", (Function<String, Boolean>) s -> Bukkit.getPluginManager().isPluginEnabled(s));

        jsEngine.put("runOpCommand", (BiConsumer<Player, String>) (p, s) -> {
            if (s.startsWith("op")) {
                ExceptionHandler.handleDanger("在"+js.getName()+"脚本文件中发现后门（获取op）,请联系附属对应作者进行处理！！！！！");
                return;
            }

            p.setOp(true);
            p.performCommand(parsePlaceholder(p, s));
            p.setOp(false);
        });

        jsEngine.put("runConsoleCommand", (Consumer<String>) s -> {
            if (s.startsWith("op")) {
                ExceptionHandler.handleDanger("在"+js.getName()+"脚本文件中发现后门（获取op）,请联系附属对应作者进行处理！！！！！");
                return;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsePlaceholder(null, s));
        });

        jsEngine.put("sendMessage", (BiConsumer<Player, String>) (p, s) -> p.sendMessage(CommonUtils.parseToComponent(parsePlaceholder(p, s))));

        //get slimefun item
        jsEngine.put("getSfItemById", (Function<String, SlimefunItem>) SlimefunItem::getById);
        jsEngine.put("getSfItemByItem", (Function<ItemStack, SlimefunItem>) SlimefunItem::getByItem);

        //randint function
        jsEngine.put("randint", (Function<Integer, Integer>) i -> new Random().nextInt(i));
        jsEngine.put("randintC", (BiFunction<Integer, Boolean, Integer>) (i, b) -> new Random().nextInt(b ? (i + 1) : i));
        jsEngine.put("randintSE", (BiFunction<Integer, Integer, Integer>) (start, end) -> {
            IntStream is = IntStream.range(start, end);
            Random random = new Random();
            int[] arr = is.toArray();
            return arr[random.nextInt(arr.length)];
        });
        jsEngine.put("randintF", (CiFunction<Integer, Integer, Boolean, Integer>) (start, end, rangeClosed) -> {
            IntStream stream = rangeClosed ? IntStream.rangeClosed(start, end) : IntStream.range(start, end);
            Random random = new Random();
            int[] arr = stream.toArray();
            return arr[random.nextInt(arr.length)];
        });

        //StorageCacheUtils functions
        jsEngine.put("setData", (CiConsumer<Location, String, String>) StorageCacheUtils::setData);
        jsEngine.put("getData", (BiFunction<Location, String, String>) StorageCacheUtils::getData);
        jsEngine.put("getBlockMenu", (Function<Location, BlockMenu>) StorageCacheUtils::getMenu);
        jsEngine.put("getBlockData", (Function<Location, SlimefunBlockData>) StorageCacheUtils::getBlock);
        jsEngine.put("isSlimefunBlock", (Function<Location, Boolean>) StorageCacheUtils::hasBlock);
        jsEngine.put("isBlock", (BiFunction<Location, String, Boolean>) StorageCacheUtils::isBlock);
        jsEngine.put("getSfItem", (Function<Location, SlimefunItem>) StorageCacheUtils::getSfItem);
    }

    public void doInit() {
        if (context == null) {
            contextInit();
        }

        evalFunction("init");
    }

    @Nullable
    @CanIgnoreReturnValue
    public Object evalFunction(String funName, Object... args) {
        if (context == null) {
            contextInit();
        }

        args = Arrays.stream(args).map(o -> {
            String fileName = js.getName();
            if (o instanceof Player p) {
                return Delegations.delegatePlayer(fileName, p);
            } else if (o instanceof Event e) {
                return Delegations.replacePlayerInEvent(fileName, e);
            } else {
                return o;
            }
        }).toArray();

        try {
            return jsEngine.invokeFunction(funName, args);
        } catch (ScriptException e) {
            ExceptionHandler.handleError("在运行"+js.getName()+"时发生错误");
            e.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        return null;
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
}