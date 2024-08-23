package org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.jvm.JavetJVMInterceptor;
import com.caoccao.javet.interception.jvm.VirtualPackage;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.reference.V8ValueGlobalObject;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class JavaScriptEval extends ScriptEval {
    private static final List<String> packages = List.of("java", "javax", "net", "org", "com", "io", "me", "de");

    private V8Runtime jsEngine;

    public JavaScriptEval(@NotNull File js, ProjectAddon addon) {
        super(js);

        reSetup();
        //setup();
        contextInit();

        addon.getScriptEvals().add(this);
    }

    private void advancedSetup() {
        addThing("SlimefunItems", SlimefunItems.class);
        addThing("SlimefunItem", SlimefunItem.class);
        addThing("StorageCacheUtils", StorageCacheUtils.class);
        addThing("SlimefunUtils", SlimefunUtils.class);
        addThing("BlockMenu", BlockMenu.class);
        addThing("PersistentDataAPI", PersistentDataAPI.class);
    }

    @Override
    public void close() {
        try {
            jsEngine.lowMemoryNotification();
            jsEngine.close();

            reSetup();
        } catch (IllegalStateException ignored) {
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public V8ValueGlobalObject getGlobalObject() {
        try {
            return jsEngine.getGlobalObject();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeContextVoid(String context) {
        try {
            jsEngine.getExecutor(context).executeVoid();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addThing(String name, Object value) {
        try {
            jsEngine.getGlobalObject().set(name, value);
        } catch (JavetException e) {
            throw new RuntimeException("Error adding thing to JavaScript global object", e);
        }
    }

    @Override
    public String key() {
        return "js";
    }

    protected final void contextInit() {
        super.contextInit();
    }

    @Nullable @CanIgnoreReturnValue
    @Override
    public Object evalFunction(String funName, Object... args) {
        return this.evalFunction(funName, true, args);
    }

    @Nullable @CanIgnoreReturnValue
    public Object evalFunction(String funName, boolean voidRunning, Object... args) {
        if (getFileContext() == null || getFileContext().isBlank()) {
            contextInit();
        }

        try {
            V8Value exists = jsEngine.getGlobalObject().get(funName);
            if (exists == null || exists instanceof V8ValueUndefined) {
                return null;
            }
        } catch (JavetException e) {
            return null;
        }

        try {
            try (V8ValueGlobalObject obj = jsEngine.getGlobalObject()) {
                if (voidRunning) {
                    obj.invokeExtended(funName, false, args);
                    return null;
                } else {
                    return obj.invokeObject(funName, args);
                }
            }
        } catch (JavetException e) {
            ExceptionHandler.handleError("在运行" + getFile().getName() + "时发生错误");
            e.printStackTrace();
        }

        return null;
    }

    private void reSetup() {
        try {
            JavetProxyConverter converter = new JavetProxyConverter();
            converter.getConfig().setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance());
            jsEngine = V8Host.getV8Instance().createV8Runtime();
            jsEngine.setConverter(converter);

            JavetJVMInterceptor interceptor = new JavetJVMInterceptor(jsEngine);
            interceptor.register(jsEngine.getGlobalObject());

            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(jsEngine);
            consoleInterceptor.register(jsEngine.getGlobalObject());

            for (String packageName : packages) {
                jsEngine.getGlobalObject().set(packageName, VirtualPackage.getPackage(jsEngine, packageName));
            }

            jsEngine.getExecutor(getFileContext()).execute();

            jsEngine.getGlobalObject().bind(new FunctionBinds());
            jsEngine.getGlobalObject().set("RunnableCreator", FunctionBinds.RunnableGetter.class);
            jsEngine.getGlobalObject().set("Java", FunctionBinds.JavaObject.class);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }

        advancedSetup();
    }
}
