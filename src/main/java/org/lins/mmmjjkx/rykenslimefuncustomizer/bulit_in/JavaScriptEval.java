package org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.js.lang.JavaScriptLanguage;
import com.oracle.truffle.js.runtime.JSRealm;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.BlockMenuUtil;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class JavaScriptEval extends ScriptEval {
    private final Context jsEngine = Context.newBuilder("js")
            .hostClassLoader(RykenSlimefunCustomizer.class.getClassLoader())
            .allowAllAccess(true)
            .allowHostAccess(UNIVERSAL_HOST_ACCESS)
            .allowNativeAccess(false)
            .allowExperimentalOptions(true)
            .allowPolyglotAccess(PolyglotAccess.ALL)
            .allowCreateProcess(true)
            .allowValueSharing(true)
            .allowIO(IOAccess.ALL)
            .allowHostClassLookup(s -> true)
            .allowHostClassLoading(true)
            .engine(Engine.newBuilder("js").allowExperimentalOptions(true).build())
            .currentWorkingDirectory(getAddon().getScriptsFolder().toPath().toAbsolutePath())
            .build();

    public JavaScriptEval(@NotNull File js, ProjectAddon addon) {
        super(js, addon);

        advancedSetup();

        setup();

        contextInit();

        addon.getScriptEvals().add(this);
    }

    private void advancedSetup() {
        JSRealm realm = JavaScriptLanguage.getJSRealm(jsEngine);
        TruffleLanguage.Env env = realm.getEnv();
        addThing("SlimefunItems", env.asHostSymbol(SlimefunItems.class));
        addThing("SlimefunItem", env.asHostSymbol(SlimefunItem.class));
        addThing("StorageCacheUtils", env.asHostSymbol(StorageCacheUtils.class));
        addThing("SlimefunUtils", env.asHostSymbol(SlimefunUtils.class));
        addThing("BlockMenu", env.asHostSymbol(BlockMenu.class));
        addThing("BlockMenuUtil", env.asHostSymbol(BlockMenuUtil.class));
        addThing("PlayerProfile", env.asHostSymbol(PlayerProfile.class));
        addThing("Slimefun", env.asHostSymbol(Slimefun.class));
    }

    @Override
    public void close() {
        try {
            jsEngine.close();
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void addThing(String name, Object value) {
        jsEngine.getBindings("js").putMember(name, value);
    }

    @Override
    public String key() {
        return "js";
    }

    private final Map<String, Value> functionCache = new ConcurrentHashMap<>();
    private final Set<String> failedFunctions = ConcurrentHashMap.newKeySet();

    @Nullable @CanIgnoreReturnValue
    @Override
    public Object evalFunction(String funName, Object... args) {
        if (failedFunctions.contains(funName)) {
            return null;
        }

        Value function = functionCache.get(funName);

        if (function == null) {
            Value bindings = jsEngine.getBindings("js");

            if (!bindings.hasMember(funName)) {
                failedFunctions.add(funName);
                return null;
            }

            Value member = bindings.getMember(funName);
            if (!member.canExecute()) {
                failedFunctions.add(funName);
                return null;
            }

            function = member;
            functionCache.put(funName, function);
        }

        try {
            Object result = function.execute(args);
            ExceptionHandler.debugLog(
                    "运行了 " + getAddon().getAddonName() + "的脚本" + getFile().getName() + "中的函数 " + funName);
            return result;
        } catch (IllegalStateException e) {
            if (!e.getMessage().contains("Multi threaded access")) {
                handleExecutionError(e, funName);
            }
        } catch (Throwable e) {
            handleExecutionError(e, funName);
        }
        return null;
    }

    private void handleExecutionError(Throwable e, String funName) {
        functionCache.remove(funName);
        failedFunctions.add(funName);

        ExceptionHandler.handleError(
                "在运行" + getAddon().getAddonName() + "的脚本" + getFile().getName() + "时发生错误", e);
    }

    protected final void contextInit() {
        super.contextInit();
        if (jsEngine != null) {
            try {
                functionCache.clear();
                failedFunctions.clear();

                jsEngine.eval(
                        Source.newBuilder("js", getFileContext(), "JavaScript").build());
            } catch (IOException e) {
                ExceptionHandler.handleError(
                        "在加载" + getAddon().getAddonName() + "的脚本" + getFile().getName() + "时发生错误", e);
            }
        }
    }
}
