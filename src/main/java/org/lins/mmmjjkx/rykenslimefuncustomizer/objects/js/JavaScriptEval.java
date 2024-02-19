package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.ban.Delegations;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.lambda.CiConsumer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.lambda.CiFunction;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class JavaScriptEval {
    private final ScriptEngine jsEngine;
    private final File js;
    private String context;
    private boolean failed = false;

    public JavaScriptEval(File js) {
        this.js = js;
        jsEngine = new NashornScriptEngineFactory().getScriptEngine();
        try {
            context = Files.readString(js.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            context = "";
            failed = true;
        }
        setup();
    }

    public void addThing(String name, Object value) {
        jsEngine.put(name, value);
    }

    private void setup() {
        jsEngine.put("server", Delegations.delegateServer(js.getName()));
        jsEngine.put("sfPlugin", Slimefun.getPlugin(Slimefun.class));

        //functions
        jsEngine.put("setData", (CiConsumer<Location, String, String>) StorageCacheUtils::setData);
        jsEngine.put("getData", (BiFunction<Location, String, String>) StorageCacheUtils::getData);

        jsEngine.put("isPluginLoaded", (Function<String, Boolean>) s -> Bukkit.getPluginManager().isPluginEnabled(s));

        jsEngine.put("runOpCommand", (BiConsumer<Player, String>) (p, s) -> {
            p.setOp(true);
            p.performCommand(parsePlaceholder(p, s));
            p.setOp(false);
        });

        jsEngine.put("runConsoleCommand", (Consumer<String>) s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsePlaceholder(null, s)));

        jsEngine.put("sendMessage", (BiConsumer<Player, String>) (p, s) -> p.sendMessage(CommonUtils.parseToComponent(parsePlaceholder(p, s))));

        //randint function
        jsEngine.put("randint", (Function<Integer, Integer>) i -> new Random().nextInt(i));
        jsEngine.put("randint", (BiFunction<Integer, Boolean, Integer>) (i, b) -> new Random().nextInt(b ? (i + 1) : i));
        jsEngine.put("randint", (BiFunction<Integer, Integer, Integer>) (start, end) -> {
            IntStream is = IntStream.range(start, end);
            Random random = new Random();
            int[] arr = is.toArray();
            return arr[random.nextInt(arr.length)];
        });
        jsEngine.put("randint", (CiFunction<Integer, Integer, Boolean, Integer>) (start, end, rangeClosed) -> {
            IntStream stream = rangeClosed ? IntStream.rangeClosed(start, end) : IntStream.range(start, end);
            Random random = new Random();
            int[] arr = stream.toArray();
            return arr[random.nextInt(arr.length)];
        });
    }

    public void doInit() {
        if (hasFunction("init")) {
            evalFunction("init");
        }
    }

    public boolean hasFunction(String funName) {
        if (jsEngine instanceof Invocable in) {
            try {
                in.invokeFunction(funName, new Object());
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            } catch (ScriptException e) {
                return true;
            }
        }
        return false;
    }

    public void evalFunction(String funName, Object... args) {
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
            context = Files.readString(js.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            context = "";
            failed = true;
        }

        try {
            jsEngine.eval(context);
        } catch (ScriptException e) {
            failed = true;
        }

        if (!failed) {
            if (jsEngine instanceof Invocable in) {
                try {
                    in.invokeFunction(funName, args);
                } catch (ScriptException e) {
                    ExceptionHandler.handleError("在运行"+js.getName()+"时发生错误");
                    e.printStackTrace();
                    failed = true;
                } catch (NoSuchMethodException e) {
                    ExceptionHandler.handleError("无法在"+js.getName()+"找到方法" + funName);
                    e.printStackTrace();
                    failed = true;
                }
            }
        } else {
            if (!js.exists()) {
                ExceptionHandler.handleError("找不到"+js.getName());
                failed = true;
                return;
            }
            String context;
            try {
                context = Files.readString(js.toPath(), StandardCharsets.UTF_8);
                failed = false;
            } catch (IOException e) {
                context = "";
            }
            try {
                jsEngine.eval(context);
                failed = false;
            } catch (ScriptException e) {
                failed = true;
                return;
            }

            evalFunction(funName, args);
        }
    }

    private String parsePlaceholder(@Nullable Player p, String text) {
        if (p != null) {
            text = text.replaceAll("%player%", p.getName());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.setPlaceholders(p, text);
        }

        return text;
    }
}