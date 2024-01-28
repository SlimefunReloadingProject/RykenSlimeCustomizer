package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

public record MachineRecord(int capacity, int totalTicks, int progressEach) implements MachineOperation {
    private static int progress = 0;

    @Override
    public void addProgress(int i) {
        if (progress >= 100) {
            progress = progressEach;
            return;
        }
        progress += progressEach;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public int getTotalTicks() {
        return totalTicks;
    }
}
