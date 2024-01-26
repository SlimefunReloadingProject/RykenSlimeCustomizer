package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.itemHandler;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import lombok.AllArgsConstructor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record.CommandOperation;

@AllArgsConstructor
public class ItemRightClick implements ItemUseHandler {
    private CommandOperation operation;
    private CustomItem item;

    @Override
    public void onRightClick(PlayerRightClickEvent playerRightClickEvent) {
        operation.run(playerRightClickEvent.getPlayer(), item);
    }
}
