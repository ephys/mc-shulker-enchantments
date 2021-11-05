package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.helpers.RenderHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ShulkerBoxTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(ShulkerBoxTileEntityRenderer.class)
@OnlyIn(Dist.CLIENT)
public class ShulkerBoxTileEntityRendererMixin {

  @Final
  @Shadow
  private ShulkerModel<?> model;

  /**
   * @author Ephys
   */
  @Overwrite
  public void render(ShulkerBoxTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int overlay) {
    Direction lvt_7_1_ = Direction.UP;
    BlockState blockState = null;
    if (tileEntity.hasWorld()) {
      BlockState _blockState = tileEntity.getBlockState();
      if (_blockState.getBlock() instanceof ShulkerBoxBlock) {
        lvt_7_1_ = _blockState.get(ShulkerBoxBlock.FACING);
        blockState = _blockState;
      }
    }

    DyeColor dyeColor = tileEntity.getColor();
    RenderMaterial renderMaterial;
    if (dyeColor == null) {
      renderMaterial = Atlases.DEFAULT_SHULKER_TEXTURE;
    } else {
      renderMaterial = Atlases.SHULKER_TEXTURES.get(dyeColor.getId());
    }

    boolean withGlint = tileEntity.getTileData().getCompound("PersistedItemNbt").contains("Enchantments");

    IVertexBuilder vertexBuilder = this.getBuffer$addGlint(renderMaterial, renderTypeBuffer, RenderType::getEntityCutoutNoCull, withGlint);
    float progress = tileEntity.getProgress(partialTicks);

    // TODO: pass enchantments from ItemStackTileEntityRenderer.func_239207_a_

    /*
     * How this works:
     * Getting the glint to show is the easy part (see getBuffer$addGlint).
     * The hard part is that, because the model has two parts (Base & Lid), the transparent glint visibly overlaps
     *  where these two parts merde. Which is ugly.
     * The workaround I've used it to define a proper Block Model for each shulker box, and these
     *  models are rendered instead of the `ShulkerModel` when the shulker is not animating (progress = 0).
     * That model being a solid block with no overlap, the glint is not duplicated.
     */

    if (progress != 0) {
      matrixStack.push();
      matrixStack.translate(0.5D, 0.5D, 0.5D);
      matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
      matrixStack.rotate(lvt_7_1_.getRotation());
      matrixStack.scale(1.0F, -1.0F, -1.0F);
      matrixStack.translate(0.0D, -1.0D, 0.0D);

      this.model.getBase().render(matrixStack, vertexBuilder, combinedLight, overlay);
      matrixStack.translate(0.0D, (-progress * 0.5F), 0.0D);
      matrixStack.rotate(Vector3f.YP.rotationDegrees(270.0F * progress));
      this.model.getLid().render(matrixStack, vertexBuilder, combinedLight, overlay);

      matrixStack.pop();
    } else {
      if (blockState == null) {
        // Rendered from inventory
        blockState = ShulkerBoxBlock.getBlockByColor(tileEntity.getColor()).getDefaultState();
      }

      RenderHelper.renderBlockModel(
        Minecraft.getInstance().getBlockRendererDispatcher(),
        blockState,
        matrixStack,
        renderTypeBuffer,
        combinedLight,
        overlay,
        withGlint
      );
    }
  }

  private IVertexBuilder getBuffer$addGlint(
    RenderMaterial renderMaterial,
    IRenderTypeBuffer bufferIn,
    Function<ResourceLocation, RenderType> renderTypeGetter,
    boolean withGlint
  ) {
    if (!withGlint) {
      return renderMaterial.getBuffer(bufferIn, renderTypeGetter);
    }

    // FIXME: tint is overlapping (not sure can be fixed)
    return new SpriteAwareVertexBuilder(
      ItemRenderer.getEntityGlintVertexBuilder(
        bufferIn,
        renderMaterial.getRenderType(renderTypeGetter),
        false,
        withGlint
      ),
      renderMaterial.getSprite()
    );
  }
}
