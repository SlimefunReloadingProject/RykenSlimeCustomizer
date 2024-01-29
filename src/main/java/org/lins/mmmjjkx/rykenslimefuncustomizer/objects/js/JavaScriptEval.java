package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.BiFunction;

public class JavaScriptEval {
    private final ScriptEngine jsEngine;
    private final File js;
    private boolean failed = false;

    public JavaScriptEval(File js) {
        this.js = js;
        ScriptEngineManager sem = new ScriptEngineManager();
        jsEngine = sem.getEngineByName("javascript");
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
        jsEngine.put("server", Bukkit.getServer());
        jsEngine.put("sfPlugin", Slimefun.getPlugin(Slimefun.class));
        jsEngine.put("setData", (CiConsumer<Location, String, String>) StorageCacheUtils::setData);
        jsEngine.put("getData", (BiFunction<Location, String, String>) StorageCacheUtils::getData);
    }

    public void evalFunction(String funName, Object... args) {
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
        }
    }
}