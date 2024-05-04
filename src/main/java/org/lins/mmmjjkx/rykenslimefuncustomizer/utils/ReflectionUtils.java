package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReflectionUtils {
    @Nullable public static <T> T getField(@Nonnull Object object, @Nonnull String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field[] fields = getAllFields(object);
        Field field = null;
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                field = f;
                break;
            }
        }
        if (field == null) throw new NoSuchFieldException(fieldName);
        field.setAccessible(true);
        return (T) field.get(object);
    }

    public static <T> void setField(@Nonnull Object object, @Nonnull String fieldName, @Nullable T value)
            throws NoSuchFieldException, IllegalAccessException {
        Field[] fields = getAllFields(object);
        Field field = null;
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                field = f;
                break;
            }
        }
        if (field == null) throw new NoSuchFieldException(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

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
}
