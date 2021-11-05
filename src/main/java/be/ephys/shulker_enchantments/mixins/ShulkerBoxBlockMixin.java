package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.helpers.ShulkerBoxBlockHelper;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends ContainerBlock {
  protected ShulkerBoxBlockMixin(Properties builder) {
    super(builder);
  }

  // ===============
  //  Add an "Open" property that we use to select which system the block will use for rendering (static plain block VS complex model)
  // ===============

  @Inject(method = "<init>", at = @At("RETURN"))
  public void ShulkerBoxBlockMixin$construct(DyeColor color, Properties properties, CallbackInfo ci) {
    this.setDefaultState(
      this.stateContainer.getBaseState()
        .with(ShulkerBoxBlock.FACING, Direction.UP)
        .with(ShulkerBoxBlockHelper.OPEN, false)
    );
  }

  @Inject(method = "fillStateContainer", at = @At("RETURN"))
  public void ShulkerBoxBlockMixin$fillStateContainer(StateContainer.Builder<Block, BlockState> builder, CallbackInfo ci) {
    builder.add(ShulkerBoxBlockHelper.OPEN);
  }

  @Overwrite()
  public BlockRenderType getRenderType(BlockState state) {
    return state.get(ShulkerBoxBlockHelper.OPEN) ? BlockRenderType.ENTITYBLOCK_ANIMATED : BlockRenderType.MODEL;
  }

  // ===============
  //  END
  // ===============

  /**
   * Restore data persisted in PersistedItemNbt to the Shulker Box ItemStack
   */
  @Inject(method = "getDrops", at = @At(value = "RETURN"))
  public void ShulkerBoxBlockMixin$getDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {

    // TODO: add enchantments to builder.withDynamicDrop instead?

    TileEntity tileEntity = builder.get(LootParameters.BLOCK_ENTITY);
    List<ItemStack> drops = cir.getReturnValue();

    if (tileEntity == null) {
      return;
    }

    CompoundNBT tileStackNbt = tileEntity.getTileData().getCompound("PersistedItemNbt");

    for (ItemStack drop : drops) {
      if (!(drop.getItem() instanceof BlockItem)) {
        continue;
      }

      BlockItem blockItem = (BlockItem) drop.getItem();
      if (!(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
        continue;
      }

      CompoundNBT existingTag = drop.getTag();
      if (existingTag == null) {
        drop.setTag(tileStackNbt.copy());
      } else {
        drop.setTag(existingTag.merge(tileStackNbt));
      }
    }
  }
}
