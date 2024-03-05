package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban.Delegations;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;

public class JavaScriptEval extends ScriptEval{
    private final GraalJSScriptEngine jsEngine;
    private final FileHandler log;

    public JavaScriptEval(@NotNull File js, ProjectAddon addon) {
        super(js, addon);
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

        File dest = new File(dir, getFile().getName()+"-%g.log");

        try {
            return new FileHandler(dest.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            jsEngine.close();
            log.close();
        } catch (IllegalStateException ignored) {}
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
        try {
            jsEngine.eval(getFileContext());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @CanIgnoreReturnValue
    public Object evalFunction(String funName, Object... args) {
        if (getFileContext() == null || getFileContext().isBlank()) {
            contextInit();
        }

        args = Arrays.stream(args).map(o -> {
            String fileName = getFile().getName();
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
            ExceptionHandler.handleError("在运行"+getFile().getName()+"时发生错误");
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {}

        return null;
    }
}