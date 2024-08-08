package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun;

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.LoopIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class AsyncChanceRecipeTask implements Runnable {
    private final Map<Integer, LoopIterator<ItemStack>> iterators = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Inventory inventory;
    private int id;

    public AsyncChanceRecipeTask() {}

    public void start(@Nonnull Inventory inv) {
        Validate.notNull(inv, "Inventory must not be null");
        this.inventory = inv;
        this.id = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(RykenSlimefunCustomizer.INSTANCE, this, 0L, 14L)
                .getTaskId();
    }

    public void add(int slot, @Nonnull List<ItemStack> item) {
        Validate.notNull(item, "Cannot add a null list of ItemStacks");
        this.lock.writeLock().lock();

        try {
            this.iterators.put(slot, new LoopIterator<>(item));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean isEmpty() {
        this.lock.readLock().lock();

        boolean var1;
        try {
            var1 = this.iterators.isEmpty();
        } finally {
            this.lock.readLock().unlock();
        }

        return var1;
    }

    public void run() {
        if (this.inventory.getViewers().isEmpty()) {
            Bukkit.getScheduler().cancelTask(this.id);
        } else {
            this.lock.readLock().lock();

            try {
                for (Map.Entry<Integer, LoopIterator<ItemStack>> entry : this.iterators.entrySet()) {
                    this.inventory.setItem(entry.getKey(), entry.getValue().next());
                }
            } finally {
                this.lock.readLock().unlock();
            }
        }
    }
}
