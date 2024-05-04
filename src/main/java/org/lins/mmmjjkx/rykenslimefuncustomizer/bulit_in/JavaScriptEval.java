package org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban.Delegations;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;

public class JavaScriptEval extends ScriptEval {
    private ScriptEngine jsEngine;
    private FileHandler log;
    private final ProjectAddon addon;

    public JavaScriptEval(@NotNull File js, ProjectAddon addon) {
        super(js, addon);
        this.addon = addon;

        log = createLogFileHandler(addon);

        jsEngine = new NashornScriptEngineFactory().getScriptEngine();
        try {
            jsEngine.eval(getFileContext());
        } catch (ScriptException e) {
            ExceptionHandler.handleError("脚本运行异常", e);
        }

        setup();
        contextInit();

        addon.getScriptEvals().add(this);
    }

    private FileHandler createLogFileHandler(ProjectAddon addon) {
        File dir = new File(addon.getScriptsFolder(), "logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, getFile().getName() + "-%g.log");

        try {
            return new FileHandler(dest.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            jsEngine = null;
            log.close();
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void restart() {
        close();
        log = createLogFileHandler(addon);

        jsEngine = new NashornScriptEngineFactory().getScriptEngine();

        setup();
        contextInit();
    }


    @Override
    public void addThing(String name, Object value) {
        jsEngine.put(name, value);
    }

    @Override
    public String key() {
        return "js";
    }

    protected final void contextInit() {
        super.contextInit();
        if (jsEngine != null) {
            try {
                jsEngine.eval(getFileContext());
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable @CanIgnoreReturnValue
    @Override
    public Object evalFunction(String funName, Object... args) {
        if (getFileContext() == null || getFileContext().isBlank()) {
            contextInit();
        }

        args = Arrays.stream(args)
                .map(o -> {
                    String fileName = getFile().getName();
                    if (o instanceof Player p) {
                        return Delegations.delegatePlayer(fileName, p);
                    } else if (o instanceof Event e) {
                        return Delegations.replacePlayerInEvent(fileName, e);
                    } else {
                        return o;
                    }
                })
                .toArray();

        try {
            return ((Invocable)jsEngine).invokeFunction(funName, args);
        } catch (ScriptException e) {
            ExceptionHandler.handleError("在运行" + getFile().getName() + "时发生错误");
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException ignored) {
        }

        return null;
    }
}
