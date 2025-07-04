package org.lins.mmmjjkx.rykenslimefuncustomizer.objects;

import java.util.Random;
import javax.annotation.Nonnull;

public record Range(int min, int max) {

    public int getRandomBetween(@Nonnull Random random) {
        return random.nextInt(min, max + 1);
    }

    public int getDistance() {
        return max - min;
    }
}
