package be.ephys.shulker_enchantments;

import be.ephys.shulker_enchantments.capabilities.BlockItemStackItemHandlerProvider;
import be.ephys.shulker_enchantments.capabilities.SeedPouchItemHandlerProvider;
import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.refill.RefillEnchantment;
import be.ephys.shulker_enchantments.siphon.SiphonEnchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Arrays;

public class ModEnchantments {

  public static final EnchantmentCategory SHULKER_LIKE = EnchantmentCategory.create("shulker_like", Tags::isShulkerLike);

  public static final SiphonEnchantment SIPHON = new SiphonEnchantment();
  public static final RefillEnchantment REFILL = new RefillEnchantment();

  @ObjectHolder("quark:seed_pouch")
  public static final Item QUARK_SEED_POUCH = null;

  public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
    CreativeModeTab.TAB_DECORATIONS.setEnchantmentCategories(
      pushToArray(CreativeModeTab.TAB_DECORATIONS.getEnchantmentCategories(), SHULKER_LIKE)
    );

    event.getRegistry().registerAll(SIPHON);
    MinecraftForge.EVENT_BUS.addListener(SIPHON::onItemPickup);
    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, ModEnchantments::onAttachItemStackCapabilities);

    //

    event.getRegistry().registerAll(REFILL);
  }

  public static void onAttachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    ItemStack stack = event.getObject();

    if (stack.is(QUARK_SEED_POUCH)) {
      event.addCapability(
        new ResourceLocation(Mod.MOD_ID, "seed_pouch_item_handler"),
        new SeedPouchItemHandlerProvider(stack)
      );

      return;
    }

    Item item = stack.getItem();

    if (!(item instanceof BlockItem blockItem)) {
      return;
    }

    if (!(blockItem.getBlock() instanceof ShulkerBoxBlock) && !(blockItem.getBlock() instanceof EnderChestBlock)) {
      return;
    }

    event.addCapability(
      new ResourceLocation(Mod.MOD_ID, "block_item_item_handler"),
      new BlockItemStackItemHandlerProvider(stack)
    );
  }

  private static <T> T[] pushToArray(T[] array, T value) {
    T[] copy = Arrays.copyOf(array, array.length + 1);
    copy[copy.length - 1] = value;

    return copy;
  }
}
