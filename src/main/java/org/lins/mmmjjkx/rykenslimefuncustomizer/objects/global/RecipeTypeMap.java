package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class RecipeTypeMap {
    private static final Map<String, RecipeType> recipeTypes;

    static {
        recipeTypes = new HashMap<>();

        RecipeTypeExpandIntegration.registerRecipeTypes();
    }

    public static void removeRecipeTypes(String... keys) {
        for (String key : keys) {
            recipeTypes.remove(key);
        }
    }

    public static void pushRecipeType(RecipeType type) {
        recipeTypes.put(type.getKey().getKey().toUpperCase(), type);
    }

    public static void pushRecipeType(List<RecipeType> types) {
        types.forEach(RecipeTypeMap::pushRecipeType);
    }

    public static void clearRecipeTypes() {
        recipeTypes.clear();
    }

    @Nullable public static RecipeType getRecipeType(String s) {
        return recipeTypes.get(s);
    }

    public enum RecipeTypeExpandIntegration {
        INFINITY_EXPANSION("io.github.mooy1.infinityexpansion.items.blocks.InfinityWorkbench", "TYPE", true),
        SLIME_TINKER("io.github.sefiraat.slimetinker.items.workstations.workbench.Workbench", "TYPE", true);

        private final String clazz;
        private final String fieldName;
        private final boolean isStatic;

        RecipeTypeExpandIntegration(String clazz, String fieldName, boolean isStatic) {
            this.clazz = clazz;
            this.fieldName = fieldName;
            this.isStatic = isStatic;
        }

        public RecipeType get() {
            try {
                Class<?> theClazz = Class.forName(clazz);
                Field field = theClazz.getDeclaredField(fieldName);
                return (RecipeType) field.get(null);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }

        static void registerRecipeTypes() {
            for (RecipeTypeExpandIntegration integration : values()) {
                String className = integration.clazz;
                String fieldName = integration.fieldName;
                try {
                    Class<?> clazz = Class.forName(className);
                    Object instance;
                    if (integration.isStatic) {
                        instance = clazz.getField(fieldName).get(null);
                    } else {
                        instance = clazz.newInstance();
                    }

                    if (instance instanceof RecipeType rt) {
                        RecipeTypeMap.pushRecipeType(rt);
                    }
                } catch (Exception e) {
                    RykenSlimefunCustomizer.INSTANCE
                            .getLogger()
                            .warning("Failed to get external recipe type from " + className + "#" + fieldName + ": "
                                    + e.getMessage());
                }
            }
        }
    }
}
