package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NBTAPIIntegration {

    public NBTAPIIntegration() {}

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
