package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.helpers.ShulkerBoxBlockHelper;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxTileEntity.class)
public class ShulkerBoxTileEntityMixin {
  @Shadow private ShulkerBoxTileEntity.AnimationStatus animationStatus;

  // ===============
  //  Control the "Open" property (see ShulkerBoxBlockMixin)
  // ===============

  @Inject(method = "func_213975_v", at = @At("RETURN"))
  public void closeInventory$setState(CallbackInfo ci) {
    World world = getThis().getWorld();
    BlockPos pos = getThis().getPos();

    if (this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.OPENING) {
      world.setBlockState(pos, world.getBlockState(pos).with(ShulkerBoxBlockHelper.OPEN, true), 1 | 2);
    } else if (this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.CLOSED) {
      world.setBlockState(pos, world.getBlockState(pos).with(ShulkerBoxBlockHelper.OPEN, false), 1 | 2);
    }
  }

  private ShulkerBoxTileEntity getThis() {
    return (ShulkerBoxTileEntity) (Object) this;
  }
}
