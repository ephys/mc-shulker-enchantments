package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.INbtAble;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// This persists enchantments put onto the Shulker Box ItemStack and saves them in the TileEntity.
// They are then retrieved back from the TileEntity when the block is transformed into an ItemStack again.

@Mixin(ShulkerBoxTileEntity.class)
@Implements(@Interface(iface = INbtAble.class, prefix = "INbtAble$"))
public class ShulkerTileEnchantMixin {

  private INBT enchantments;

  public void INbtAble$readFromItemStackNbt(CompoundNBT nbtIn) {
    INBT nbt = nbtIn.get("Enchantments");
    if (nbt != null) {
      this.enchantments = nbt.copy();
    }
  }

  public CompoundNBT INbtAble$writeForItemStackNbt() {
    CompoundNBT nbtOut = new CompoundNBT();
    if (enchantments != null) {
      nbtOut.put("Enchantments", this.enchantments);
    }

    return nbtOut;
  }

  // persist enchantments in TileEntity NBT

  @Inject(method = "write", at = @At("RETURN"))
  public void write$AddEnchantments(CompoundNBT compound, CallbackInfoReturnable<CompoundNBT> cir) {
    if (enchantments != null) {
      compound.put("Enchantments", enchantments);
    }
  }

  @Inject(method = "read", at = @At("RETURN"))
  public void read$AddEnchantments(BlockState p_230337_1_, CompoundNBT nbtIn, CallbackInfo ci) {
    INBT nbt = nbtIn.get("Enchantments");
    if (nbt != null) {
      this.enchantments = nbt.copy();
    }
  }
}
