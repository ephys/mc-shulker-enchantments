package be.ephys.shulker_enchantments.helpers;

import be.ephys.shulker_enchantments.core.Mod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockItemMixinHelper {
  public static void setBlockEntityEnchantments(Level worldIn, BlockPos pos, ItemStack stackIn) {
    CompoundTag stackNbt = stackIn.getTag();
    if (stackNbt == null) {
      return;
    }

    if (!stackNbt.contains("Enchantments")) {
      return;
    }

    BlockEntity blockEntity = worldIn.getBlockEntity(pos);
    if (blockEntity == null) {
      return;
    }

    CompoundTag persistedItemNbt = new CompoundTag();
    boolean copied = copyTo(persistedItemNbt, stackNbt, new String[]{"RepairCost", "Enchantments", "quark:RuneColor", "quark:RuneAttached"});
    if (!copied) {
      return;
    }

    CompoundTag blockNbt = blockEntity.getPersistentData();
    blockNbt.put(Mod.PERSISTED_ITEM_NBT_TAG_ID, persistedItemNbt);
  }

  private static boolean copyTo(CompoundTag target, CompoundTag source, String[] keys) {
    boolean copied = false;
    for (String key : keys) {
      if (source.contains(key)) {
        target.put(key, source.get(key));
        copied = true;
      }
    }

    return copied;
  }
}
