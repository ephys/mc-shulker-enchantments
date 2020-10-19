package be.ephys.shulker_enchantments;

import net.minecraft.nbt.CompoundNBT;

/**
 * This interface can be put on TileEntities to be able to alter the state of the tile entity
 * based on the NBT Tag of the ItemStack used to place it,
 * And alter the state of an ItemStack when it is created from a block with a TileEntity
 */
public interface INbtAble {
  /**
   * Use this method to alter the state of the TileEntity based on the NBT of the ItemStack
   * used to create the TileEntity.
   *
   * @param nbtIn The NBT tag of the ItemStack
   */
  void readFromItemStackNbt(CompoundNBT nbtIn);

  /**
   * Use this method to alter the state of the NBT of the ItemStack that will be created
   * when this tile is broken.
   *
   * @return The CompoundNBT to merge with the CompoundNBT of the ItemStack
   */
  CompoundNBT writeForItemStackNbt();
}
