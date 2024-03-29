package be.ephys.shulker_enchantments.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Stores a ItemStack's enchantment to its block version.
 */
@Mixin(BlockItem.class)
public class BlockItemMixin {
  @Inject(method = "updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
  private static void setTileEntityNBT$handleINbtAble(Level worldIn, Player player, BlockPos pos, ItemStack stackIn, CallbackInfoReturnable<Boolean> cir) {
    CompoundTag nbt = stackIn.getTag();
    if (nbt == null) {
      return;
    }

    if (!nbt.contains("Enchantments")) {
      return;
    }

    BlockEntity blockEntity = worldIn.getBlockEntity(pos);
    if (blockEntity == null) {
      return;
    }

    CompoundTag persistedItemNbt = new CompoundTag();
    boolean copied = copyTo(persistedItemNbt, nbt, new String[]{"RepairCost", "Enchantments", "quark:RuneColor", "quark:RuneAttached"});
    if (!copied) {
      return;
    }

    blockEntity.getTileData().put("PersistedItemNbt", persistedItemNbt);
    blockEntity.setChanged();
  }

  private static boolean copyTo(CompoundTag target, CompoundTag source, String[] keys) {
    boolean copied = false;
    for (String key: keys) {
      if (source.contains(key)) {
        target.put(key, source.get(key));
        copied = true;
      }
    }

    return copied;
  }
}
