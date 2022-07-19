package be.ephys.shulker_enchantments;

import be.ephys.shulker_enchantments.refill.RefillEnchantment;
import be.ephys.shulker_enchantments.siphon.SiphonEnchantment;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;

import java.util.Arrays;

public class ModEnchantments {

  public static final EnchantmentCategory SHULKER_LIKE = EnchantmentCategory.create("shulker_like", ShulkerLikeTag::isShulkerLike);

  public static final SiphonEnchantment SIPHON = new SiphonEnchantment();
  public static final RefillEnchantment REFILL = new RefillEnchantment();

  public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
    CreativeModeTab.TAB_DECORATIONS.setEnchantmentCategories(
      pushToArray(CreativeModeTab.TAB_DECORATIONS.getEnchantmentCategories(), SHULKER_LIKE)
    );

    event.getRegistry().registerAll(SIPHON);
    MinecraftForge.EVENT_BUS.addListener(SIPHON::onItemPickup);
    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, SIPHON::onAttachItemStackCapabilities);

    //

    event.getRegistry().registerAll(REFILL);
  }

  private static <T> T[] pushToArray(T[] array, T value) {
    T[] copy = Arrays.copyOf(array, array.length + 1);
    copy[copy.length - 1] = value;

    return copy;
  }
}
