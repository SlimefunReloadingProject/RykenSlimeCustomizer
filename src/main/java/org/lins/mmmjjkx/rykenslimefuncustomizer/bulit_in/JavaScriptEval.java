package org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.js.lang.JavaScriptLanguage;
import com.oracle.truffle.js.runtime.JSRealm;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.FileHandler;
import javax.script.ScriptException;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.io.IOAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class JavaScriptEval extends ScriptEval {
    private static final File PLUGINS_FOLDER = RykenSlimefunCustomizer.INSTANCE.getDataFolder().getParentFile();

    private final ProjectAddon addon;

    private GraalJSScriptEngine jsEngine;
    private FileHandler log;

    public JavaScriptEval(@NotNull File js, ProjectAddon addon) {
        super(js);

        this.addon = addon;

        reSetup();

        setup();

        contextInit();

        addon.getScriptEvals().add(this);
    }

    private void advancedSetup() {
        JSRealm realm = JavaScriptLanguage.getJSRealm(jsEngine.getPolyglotContext());
        TruffleLanguage.Env env = realm.getEnv();
        addThing("SlimefunItems", env.asHostSymbol(SlimefunItems.class));
        addThing("SlimefunItem", env.asHostSymbol(SlimefunItem.class));
        addThing("StorageCacheUtils", env.asHostSymbol(StorageCacheUtils.class));
        addThing("SlimefunUtils", env.asHostSymbol(SlimefunUtils.class));
        addThing("BlockMenu", env.asHostSymbol(BlockMenu.class));

        for (File file : Objects.requireNonNull(PLUGINS_FOLDER.listFiles())) {
            TruffleFile truffleFile = env.getPublicTruffleFile(file.toURI());
            if (!truffleFile.isDirectory() && truffleFile.endsWith(".jar")) {
                env.addToHostClassPath(truffleFile);
            }
        }
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
            jsEngine.close();
            log.close();

            reSetup();
        } catch (IllegalStateException ignored) {
        }
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

        try {
            return jsEngine.invokeFunction(funName, args);
        } catch (ScriptException e) {
            ExceptionHandler.handleError("在运行" + getFile().getName() + "时发生错误");
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {
        }

        return null;
    }

    private void reSetup() {
        log = createLogFileHandler(addon);

        jsEngine = GraalJSScriptEngine.create(
                null,
                Context.newBuilder("js")
                        .allowAllAccess(true)
                        .allowHostAccess(UNIVERSAL_HOST_ACCESS)
                        .allowNativeAccess(false)
                        .allowExperimentalOptions(true)
                        .allowPolyglotAccess(PolyglotAccess.ALL)
                        .allowCreateProcess(true)
                        .allowIO(IOAccess.ALL)
                        .allowHostClassLookup(s -> {
                            if (s.equalsIgnoreCase("org.bukkit.Bukkit")) {
                                return false;
                            } else if (s.contains("org.bukkit.craftbukkit") && s.endsWith("CraftServer")) {
                                return false;
                            }
                            return !s.equalsIgnoreCase("net.minecraft.server.MinecraftServer");
                        })
                        .logHandler(log));

        advancedSetup();
    }
}