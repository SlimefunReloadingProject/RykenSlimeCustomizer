package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import java.util.Random;
import javax.annotation.Nonnull;
import lombok.Getter;

@Getter
public class Range {
    private final int min;
    private final int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getRandomBetween(@Nonnull Random random) {
        return random.nextInt(min, max + 1);
    }

    public int getDistance() {
        return max - min;
    }
}
