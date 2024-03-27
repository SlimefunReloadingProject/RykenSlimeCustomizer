package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun;

import io.github.thebusybiscuit.slimefun4.core.attributes.WitherProof;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Wither;

import javax.annotation.Nonnull;

public interface WitherProofBlockImpl extends WitherProof {
    default void onAttack(@Nonnull Block var1, @Nonnull Wither var2){}
}
