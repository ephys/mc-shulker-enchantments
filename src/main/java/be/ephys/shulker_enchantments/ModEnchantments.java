package be.ephys.shulker_enchantments;

import be.ephys.shulker_enchantments.capabilities.BlockItemStackItemHandlerProvider;
import be.ephys.shulker_enchantments.capabilities.SeedPouchItemHandlerProvider;
import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.refill.RefillEnchantment;
import be.ephys.shulker_enchantments.siphon.SiphonEnchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = Mod.MOD_ID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
)
public class ModEnchantments {

  public static final EnchantmentCategory SHULKER_LIKE = EnchantmentCategory.create("shulker_like", Tags::isShulkerLike);

  public static final SiphonEnchantment SIPHON = new SiphonEnchantment();
  public static final RefillEnchantment REFILL = new RefillEnchantment();

  public static final RegistryObject<Item> QUARK_SEED_POUCH = RegistryObject.create(ResourceLocation.fromNamespaceAndPath("quark", "seed_pouch"), ForgeRegistries.ITEMS);

  public static final ResourceLocation BLOCK_ITEM_HANDLER_CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "block_item_item_handler");
  public static final ResourceLocation SEED_POUCH_ITEM_HANDLER_CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "seed_pouch_item_handler");

  @SubscribeEvent
  public static void registerEnchantments(RegisterEvent event) {
    event.register(ForgeRegistries.Keys.ENCHANTMENTS, helper -> {
      helper.register(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "siphon"), SIPHON);
      helper.register(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "refill"), REFILL);
    });

    MinecraftForge.EVENT_BUS.addListener(SIPHON::onItemPickup);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, SIPHON::onItemUseFinish);
    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, ModEnchantments::onAttachItemStackCapabilities);
  }

  public static void onAttachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    ItemStack stack = event.getObject();

    if (QUARK_SEED_POUCH.isPresent() && stack.is(QUARK_SEED_POUCH.get())) {
      if (event.getCapabilities().containsKey(SEED_POUCH_ITEM_HANDLER_CAPABILITY_ID)) {
        return;
      }

      event.addCapability(
        SEED_POUCH_ITEM_HANDLER_CAPABILITY_ID,
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

    if (event.getCapabilities().containsKey(BLOCK_ITEM_HANDLER_CAPABILITY_ID)) {
      return;
    }

    event.addCapability(
      BLOCK_ITEM_HANDLER_CAPABILITY_ID,
      new BlockItemStackItemHandlerProvider(stack)
    );
  }
}
