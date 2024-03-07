package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeTypeMap {
    private static final Map<String, RecipeType> recipeTypes;

    static {
        recipeTypes = new HashMap<>();
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

    @Nullable
    public static RecipeType getRecipeType(String s) {
        return recipeTypes.get(s);
    }
}
