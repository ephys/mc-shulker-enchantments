package be.ephys.shulker_enchantments;

import be.ephys.shulker_enchantments.core.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShulkerLikeTag {
  public static final ResourceLocation TAG = new ResourceLocation(Mod.MOD_ID, "shulker_like");
  public static final TagKey<Item> TAG_WRAPPER = ItemTags.create(TAG);

  public static boolean isShulkerLike(Item item) {
    return item.builtInRegistryHolder().is(TAG_WRAPPER);
  }

  public static boolean isShulkerLike(ItemStack item) {
    return item.is(TAG_WRAPPER);
  }
}
