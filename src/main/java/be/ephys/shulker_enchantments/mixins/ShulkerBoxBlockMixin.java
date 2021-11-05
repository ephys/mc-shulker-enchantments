package be.ephys.shulker_enchantments.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends ContainerBlock {
  protected ShulkerBoxBlockMixin(Properties builder) {
    super(builder);
  }

  /**
   * Restore data persisted in PersistedItemNbt to the Shulker Box ItemStack
   */
  @Inject(method = "getDrops", at = @At(value = "RETURN"))
  public void getDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {

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
