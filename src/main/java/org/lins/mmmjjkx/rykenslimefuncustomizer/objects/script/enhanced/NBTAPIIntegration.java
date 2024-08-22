package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced;

import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NBTAPIIntegration {
    public static NBTAPIIntegration instance = new NBTAPIIntegration();

    private NBTAPIIntegration() {}

    public NBTItem readItem(ItemStack item) {
        return new NBTItem(item.clone());
    }

    public NBTCompound getOrCreateCompound(NBTCompound parent, String name) {
        return parent.getOrCreateCompound(name);
    }

    public NBTBlock readBlock(Block block) {
        return new NBTBlock(block);
    }

    public NBTEntity readEntity(Entity entity) {
        return new NBTEntity(entity);
    }
}
