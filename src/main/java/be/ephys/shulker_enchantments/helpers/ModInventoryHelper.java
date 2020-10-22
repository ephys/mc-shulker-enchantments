package be.ephys.shulker_enchantments.helpers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class ModInventoryHelper {

  public static boolean areItemStacksEqual(ItemStack a, ItemStack b) {
    return (a.isEmpty() && b.isEmpty()) || ItemHandlerHelper.canItemStacksStack(a, b);
  }
}
