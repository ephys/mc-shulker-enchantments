package be.ephys.shulker_enchantments;

import be.ephys.shulker_enchantments.core.Mod;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CopyEnchantmentsLootModifier extends LootModifier {

  public static final Supplier<Codec<CopyEnchantmentsLootModifier>> CODEC = Suppliers.memoize(() ->
    RecordCodecBuilder.create(
      inst -> LootModifier.codecStart(inst).apply(inst, CopyEnchantmentsLootModifier::new)
    )
  );

  public CopyEnchantmentsLootModifier(LootItemCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> drops, LootContext context) {
    if (!context.hasParam(LootContextParams.BLOCK_ENTITY)) {
      return drops;
    }

    BlockEntity tileEntity = context.getParam(LootContextParams.BLOCK_ENTITY);
    if (!(tileEntity instanceof ShulkerBoxBlockEntity) && !(tileEntity instanceof EnderChestBlockEntity)) {
      return drops;
    }

    CompoundTag tileStackNbt = tileEntity.getPersistentData().getCompound(Mod.PERSISTED_ITEM_NBT_TAG_ID);

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

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC.get();
  }
}
