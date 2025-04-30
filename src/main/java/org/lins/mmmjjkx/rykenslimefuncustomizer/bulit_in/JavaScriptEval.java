package org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.js.lang.JavaScriptLanguage;
import com.oracle.truffle.js.runtime.JSRealm;
import com.oracle.truffle.js.runtime.objects.JSAttributes;
import com.oracle.truffle.js.runtime.objects.JSObject;
import com.oracle.truffle.js.runtime.objects.JSObjectUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    private static final File PLUGINS_FOLDER =
            RykenSlimefunCustomizer.INSTANCE.getDataFolder().getParentFile();
    private final Set<String> failed_functions = new HashSet<>();

    private Context jsEngine;

    public JavaScriptEval(@NotNull File js, ProjectAddon addon) {
        super(js, addon);
        reSetup();

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

        for (File file : Objects.requireNonNull(PLUGINS_FOLDER.listFiles())) {
            TruffleFile truffleFile = env.getPublicTruffleFile(file.toURI());
            if (!truffleFile.isDirectory() && truffleFile.getName().endsWith(".jar")) {
                env.addToHostClassPath(truffleFile);
            }
        }

        JSObject java = JSObjectUtil.createOrdinaryPrototypeObject(realm);
        JSObjectUtil.putToStringTag(java, JSRealm.JAVA_CLASS_NAME);

        JSObjectUtil.putDataProperty(realm.getGlobalObject(), "Java", java, JSAttributes.getDefaultNotEnumerable());

        jsEngine.enter();
    }

    @Override
    public void close() {
        try {
            jsEngine.leave();
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

    protected final void contextInit() {
        super.contextInit();
        if (jsEngine != null) {
            try {
                jsEngine.eval(
                        Source.newBuilder("js", getFileContext(), "JavaScript").build());
            } catch (IOException e) {
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

        // a simple fix for the optimization
        if (failed_functions.contains(funName)) {
            return null;
        }

        Value member = jsEngine.getBindings("js").getMember(funName);
        if (member == null) {
            failed_functions.add(funName);
            return null;
        }

        try {
            Object result = member.execute(args);
            ExceptionHandler.debugLog(
                    "运行了 " + getAddon().getAddonName() + "的脚本" + getFile().getName() + "中的函数 " + funName);
            return result;
        } catch (IllegalStateException e) {
            String message = e.getMessage();
            if (!message.contains("Multi threaded access")) {
                ExceptionHandler.handleError(
                        "在运行附属" + getAddon().getAddonName() + "的脚本" + getFile().getName() + "时发生错误");
                e.printStackTrace();
            }
        } catch (Throwable e) {
            ExceptionHandler.handleError(
                    "在运行" + getAddon().getAddonName() + "的脚本" + getFile().getName() + "时发生意外错误");
            e.printStackTrace();
        }

        return null;
    }

    private void reSetup() {
        jsEngine = Context.newBuilder("js")
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

        advancedSetup();
    }
}
