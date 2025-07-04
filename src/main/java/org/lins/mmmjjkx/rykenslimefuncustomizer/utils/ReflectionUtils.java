package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class ReflectionUtils {
    @Nonnull
    public static Field[] getAllFields(@Nonnull Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    @Nonnull
    public static Method[] getAllMethods(@Nonnull Object object) {
        Class<?> clazz = object.getClass();
        List<Method> methodList = new ArrayList<>();
        while (clazz != null) {
            methodList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods())));
            clazz = clazz.getSuperclass();
        }
        Method[] methods = new Method[methodList.size()];
        methodList.toArray(methods);
        return methods;
    }
}
