package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.ban.Delegations;
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
import java.util.function.BiFunction;

public class JavaScriptEval {
    private final ScriptEngine jsEngine;
    private final File js;
    private boolean failed = false;

    public JavaScriptEval(File js) {
        this.js = js;
        jsEngine = new NashornScriptEngineFactory().getScriptEngine();
        String context;
        try {
            context = Files.readString(js.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            context = "";
            failed = true;
        }
        setup();
        try {
            jsEngine.eval(context);
        } catch (ScriptException e) {
            failed = true;
        }
    }

    public void addThing(String name, Object value) {
        jsEngine.put(name, value);
    }

    private void setup() {
        jsEngine.put("server", Delegations.delegateServer(js.getName()));
        jsEngine.put("sfPlugin", Slimefun.getPlugin(Slimefun.class));
        jsEngine.put("setData", (CiConsumer<Location, String, String>) StorageCacheUtils::setData);
        jsEngine.put("getData", (BiFunction<Location, String, String>) StorageCacheUtils::getData);
    }

    public boolean hasFunction(String funName, int argSize) {
        if (jsEngine instanceof Invocable in) {
            try {
                in.invokeFunction(funName, new Object[argSize]);
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
            } else if (o instanceof PlayerEvent pe) {
                return Delegations.replacePlayerInEvent(fileName, pe);
            } else {
                return o;
            }
        }).toArray();

        if (!failed) {
            if (jsEngine instanceof Invocable in) {
                try {
                    in.invokeFunction(funName, args);
                } catch (ScriptException e) {
                    ExceptionHandler.handleError("在运行"+js.getName()+"时发生错误");
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    ExceptionHandler.handleError("无法在"+js.getName()+"找到方法" + funName);
                    e.printStackTrace();
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
                return;
            }
            try {
                jsEngine.eval(context);
                failed = false;
            } catch (ScriptException e) {
                failed = true;
            }

            if (!failed) {
                evalFunction(funName, args);
            }
        }
    }
}