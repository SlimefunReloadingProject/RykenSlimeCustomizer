package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced;

import de.tr7zw.nbtapi.*;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NBTAPIIntegration {
    public static NBTAPIIntegration instance = new NBTAPIIntegration();

    private NBTAPIIntegration() {}

    public ReadWriteNBT readItem(ItemStack item) {
        return NBT.itemStackToNBT(item);
    }

    public NBTCompound getOrCreateCompound(NBTCompound parent, String name) {
        return parent.getOrCreateCompound(name);
    }

    public NBTCompound readBlock(Block block) {
        return new NBTBlock(block).getData();
    }

    public ReadWriteNBT readEntity(Entity entity) {
        ReadWriteNBT entityNbt = NBT.createNBTObject();
        NBT.get(entity, entityNbt::mergeCompound);
        return entityNbt;
    }

    public ReadWriteNBT createCompound() {
        return NBT.createNBTObject();
    }
}
