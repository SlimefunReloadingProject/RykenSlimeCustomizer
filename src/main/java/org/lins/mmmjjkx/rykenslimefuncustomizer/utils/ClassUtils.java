package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import org.jetbrains.annotations.Nullable;

public class ClassUtils {
    private static final Map<String, Class<?>> cache = new HashMap<>();

    public static Class<?> generateClass(
            Class<?> extendClass,
            String centerName,
            String nameReplacement,
            Class<?>[] interfaces,
            @Nullable Function<DynamicType.Builder<?>, DynamicType.Builder<?>> delegation) {

        String finalClassName = extendClass.getSimpleName().replace(nameReplacement, "") + centerName + nameReplacement;
        if (cache.containsKey(finalClassName)) {
            return cache.get(finalClassName);
        }

        DynamicType.Builder<?> builder = new ByteBuddy().subclass(extendClass);

        if (delegation != null) {
            builder = delegation.apply(builder);
        }

        builder = builder.implement(interfaces).name(finalClassName);

        DynamicType.Unloaded<?> unloaded = builder.make();
        Class<?> clazz = unloaded.load(extendClass.getClassLoader()).getLoaded();
        cache.put(finalClassName, clazz);
        return clazz;
    }
}
