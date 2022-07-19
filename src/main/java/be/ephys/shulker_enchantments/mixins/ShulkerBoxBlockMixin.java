package be.ephys.shulker_enchantments.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends BaseEntityBlock {
  protected ShulkerBoxBlockMixin(Properties builder) {
    super(builder);
  }

  /**
   * Restore data persisted in PersistedItemNbt to the Shulker Box ItemStack
   */
  @Inject(method = "getDrops", at = @At(value = "RETURN"))
  public void ShulkerBoxBlockMixin$getDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {

    // TODO: add enchantments to builder.withDynamicDrop instead?

    BlockEntity tileEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
    List<ItemStack> drops = cir.getReturnValue();

    if (tileEntity == null) {
      return;
    }

    CompoundTag tileStackNbt = tileEntity.getTileData().getCompound("PersistedItemNbt");

    for (ItemStack drop : drops) {
      if (!(drop.getItem() instanceof BlockItem)) {
        continue;
      }

      BlockItem blockItem = (BlockItem) drop.getItem();
      if (!(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
        continue;
      }

      CompoundTag existingTag = drop.getTag();
      if (existingTag == null) {
        drop.setTag(tileStackNbt.copy());
      } else {
        drop.setTag(existingTag.merge(tileStackNbt));
      }
    }
  }
}
