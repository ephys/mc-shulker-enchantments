package be.ephys.shulker_enchantments.helpers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {

  /**
   * Works like
   * BlockRendererDispatcher#renderBlock(blockState, matrixStack, renderTypeBuffer, combinedLight, overlay);
   * But we force the "Model" version to render instead of the block entity
   */
  public static void renderBlockModel(
    BlockRendererDispatcher rendererDispatcher,
    BlockState blockState,
    MatrixStack matrixStack,
    IRenderTypeBuffer bufferType,
    int combinedLight,
    int combinedOverlay,
    boolean withGlint
  ) {
    BlockModelRenderer renderer = rendererDispatcher.getBlockModelRenderer();

    IBakedModel ibakedmodel = rendererDispatcher.getModelForState(blockState);
    int i = rendererDispatcher.blockColors.getColor(blockState, null, null, 0);
    float f = (float) (i >> 16 & 255) / 255.0F;
    float f1 = (float) (i >> 8 & 255) / 255.0F;
    float f2 = (float) (i & 255) / 255.0F;

    RenderType renderType = RenderTypeLookup.func_239220_a_(blockState, false);

    renderer.renderModel(
      matrixStack.getLast(),
      ItemRenderer.getEntityGlintVertexBuilder(bufferType, renderType, false, withGlint),
      blockState,
      ibakedmodel,
      f,
      f1,
      f2,
      combinedLight,
      combinedOverlay,
      EmptyModelData.INSTANCE
    );
  }
}
