package be.ephys.shulker_enchantments;

import be.ephys.shulker_enchantments.core.Mod;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ShulkerLikeTag {
  public static final ResourceLocation TAG = new ResourceLocation(Mod.MOD_ID, "shulker_like");
  public static final ITag.INamedTag<Item> TAG_WRAPPER = ItemTags.makeWrapperTag(TAG.toString());

  public static boolean isShulkerLike(Item item) {
//    TAG_WRAPPER
    return item.isIn(TAG_WRAPPER);
  }
}
