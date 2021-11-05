package be.ephys.shulker_enchantments.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Stores a ItemStack's enchantment to its block version.
 */
@Mixin(BlockItem.class)
public class BlockItemMixin {
  @Inject(method = "setTileEntityNBT", at = @At("RETURN"))
  private static void setTileEntityNBT$handleINbtAble(World worldIn, PlayerEntity player, BlockPos pos, ItemStack stackIn, CallbackInfoReturnable<Boolean> cir) {
    CompoundNBT nbt = stackIn.getTag();
    if (nbt == null) {
      return;
    }

    ListNBT enchantments = nbt.getList("Enchantments", 10);
    if (!enchantments.isEmpty()) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity == null) {
        return;
      }

      CompoundNBT persistedItemNbt = new CompoundNBT();
      persistedItemNbt.put("Enchantments", enchantments);
      tileentity.getTileData().put("PersistedItemNbt", persistedItemNbt);
      tileentity.markDirty();
    }
  }
}
