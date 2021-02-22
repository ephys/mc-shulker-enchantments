package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.INbtAble;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SpriteAwareVertexBuilder;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ShulkerBoxTileEntityRenderer;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(ShulkerBoxTileEntityRenderer.class)
@OnlyIn(Dist.CLIENT)
public class ShulkerBoxTileEntityRendererMixin {

  // give shulker boxes glint
  @Redirect(
    method = "render",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/renderer/model/RenderMaterial;getBuffer(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
    )
  )
  private IVertexBuilder getBuffer$addGlint(RenderMaterial renderMaterial, IRenderTypeBuffer bufferIn, Function<ResourceLocation, RenderType> renderTypeGetter, ShulkerBoxTileEntity shulkerBlockEntity) {
    if (!(shulkerBlockEntity instanceof INbtAble)) {
      return renderMaterial.getBuffer(bufferIn, renderTypeGetter);
    }

    // TODO: pass enchantments from ItemStackTileEntityRenderer.func_239207_a_
    // TODO
    boolean hasEnchantments = false; //((INbtAble) shulkerBlockEntity).writeForItemStackNbt().contains("Enchantments");

    // FIXME: tint is overlapping (not sure can be fixed)
    return new SpriteAwareVertexBuilder(
      ItemRenderer.getEntityGlintVertexBuilder(
        bufferIn,
        renderMaterial.getRenderType(renderTypeGetter),
        false,
        hasEnchantments
      ),
      renderMaterial.getSprite()
    );
  }
}
