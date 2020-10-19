package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.INbtAble;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin is part of the implementation of INbtAble
 *
 * This mixin passes the NBT Tag of ItemStacks to the TileEntity of the block the ItemStack created,
 * if the TileEntity implements INbtAble
 */
@Mixin(BlockItem.class)
public class BlockItemMixin {
  @Inject(method = "setTileEntityNBT", at = @At("RETURN"))
  private static void setTileEntityNBT$handleINbtAble(World worldIn, PlayerEntity player, BlockPos pos, ItemStack stackIn, CallbackInfoReturnable<Boolean> cir) {
    MinecraftServer minecraftserver = worldIn.getServer();
    if (minecraftserver == null) {
      return;
    }

    CompoundNBT nbt = stackIn.getTag();
    if (nbt == null) {
      return;
    }

    TileEntity tileentity = worldIn.getTileEntity(pos);
    if (tileentity instanceof INbtAble) {
      ((INbtAble) tileentity).readFromItemStackNbt(nbt);
    }
  }
}
