package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.generations;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Getter;

@Getter
public class GenerationInfo {
    private final SlimefunItemStack slimefunItemStack;
    private final List<GenerationArea> areas;

    public GenerationInfo(@Nonnull SlimefunItemStack slimefunItemStack, @Nonnull List<GenerationArea> areas) {
        this.slimefunItemStack = slimefunItemStack;
        this.areas = areas;
    }
}
