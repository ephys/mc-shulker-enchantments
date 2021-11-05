package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.helpers.ShulkerBoxBlockHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ShulkerBoxTileEntityRenderer;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxTileEntityRenderer.class)
@OnlyIn(Dist.CLIENT)
public class ShulkerBoxTileEntityRendererMixin {

  // ===============
  //  Prevent Tile Entity rendering if the "Open" property is set (the Baked Model will be used instead)
  // ===============

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void render$cancelStatic(
    ShulkerBoxTileEntity tileEntity,
    float partialTicks,
    MatrixStack matrixStack,
    IRenderTypeBuffer renderTypeBuffer,
    int combinedLight,
    int overlay,
    CallbackInfo callbackInfo
  ) {
    // don't render the TileEntity if the shulker is not open
    if (tileEntity.hasWorld()) {
      BlockState blockstate = tileEntity.getWorld().getBlockState(tileEntity.getPos());
      if (blockstate.hasProperty(ShulkerBoxBlockHelper.OPEN) && !blockstate.get(ShulkerBoxBlockHelper.OPEN)) {
        callbackInfo.cancel();
      }
    }
  }
}
