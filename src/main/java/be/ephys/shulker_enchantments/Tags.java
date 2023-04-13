package be.ephys.shulker_enchantments;

import be.ephys.shulker_enchantments.core.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Tags {
  public static final TagKey<Item> TAG_SHULKER_LIKE = ItemTags.create(new ResourceLocation(Mod.MOD_ID, "shulker_like"));
  public static final TagKey<Item> TAG_ENDER_CHESTS = ItemTags.create(new ResourceLocation(Mod.MOD_ID, "ender_chests"));
  public static final TagKey<Item> TAG_NON_REFILLABLE = ItemTags.create(new ResourceLocation(Mod.MOD_ID, "non_refillable"));

  public static boolean isShulkerLike(Item item) {
    return item.builtInRegistryHolder().is(TAG_SHULKER_LIKE);
  }

  public static boolean isShulkerLike(ItemStack item) {
    return item.is(TAG_SHULKER_LIKE);
  }

  public static boolean isEnderChest(Item item) {
    return item.builtInRegistryHolder().is(TAG_ENDER_CHESTS);
  }

  public static boolean isEnderChest(ItemStack item) {
    return item.is(TAG_ENDER_CHESTS);
  }
}
