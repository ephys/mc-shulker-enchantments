package be.ephys.shulker_enchantments;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class CopyEnchantmentsLootModifier  extends LootModifier {

  public CopyEnchantmentsLootModifier(LootItemCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Nonnull
  @Override
  protected List<ItemStack> doApply(List<ItemStack> drops, LootContext context) {
    if (!context.hasParam(LootContextParams.BLOCK_ENTITY)) {
      return drops;
    }

    BlockEntity tileEntity = context.getParam(LootContextParams.BLOCK_ENTITY);
    if (!(tileEntity instanceof ShulkerBoxBlockEntity) && !(tileEntity instanceof EnderChestBlockEntity)) {
      return drops;
    }

    CompoundTag tileStackNbt = tileEntity.getTileData().getCompound("PersistedItemNbt");

    for (ItemStack drop : drops) {
      if (!(drop.getItem() instanceof BlockItem)) {
        continue;
      }

      BlockItem blockItem = (BlockItem) drop.getItem();
      if (!(blockItem.getBlock() instanceof ShulkerBoxBlock) && !(blockItem.getBlock() instanceof EnderChestBlock)) {
        continue;
      }

      CompoundTag existingTag = drop.getTag();
      if (existingTag == null) {
        drop.setTag(tileStackNbt.copy());
      } else {
        drop.setTag(existingTag.merge(tileStackNbt));
      }
    }

    return drops;
  }

  public static class Serializer extends GlobalLootModifierSerializer<CopyEnchantmentsLootModifier> {

    @Override
    public CopyEnchantmentsLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
      return new CopyEnchantmentsLootModifier(conditions);
    }

    @Override
    public JsonObject write(CopyEnchantmentsLootModifier instance) {
      return this.makeConditions(instance.conditions);
    }
  }

}
