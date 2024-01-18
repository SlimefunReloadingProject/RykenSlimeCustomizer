package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomMenu extends BlockMenuPreset {
    private int line;

    public CustomMenu(@NotNull String id, @NotNull String title, int line) {
        super(id, title);
    }

    @Override
    public void init() {

    }

    @Override
    public boolean canOpen(@NotNull Block block, @NotNull Player player) {
        return false;
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
        return new int[0];
    }
}
