package be.ephys.shulker_enchantments.siphon;

import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.ShulkerLikeTag;
import be.ephys.shulker_enchantments.capabilities.ItemStackHelperItemHandlerProvider;
import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.helpers.ModInventoryHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Iterator;
import java.util.Optional;

public class SiphonEnchantment extends Enchantment {
  public SiphonEnchantment() {
    super(Rarity.RARE, ModEnchantments.SHULKER_LIKE, new EquipmentSlotType[0]);
    setRegistryName(Mod.MOD_ID + ":siphon");
    this.name = "enchantment." + Mod.MOD_ID + ".siphon";
  }

  @Override
  public boolean canApply(ItemStack stack) {
    return ShulkerLikeTag.isShulkerLike(stack.getItem());
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isTreasureEnchantment() {
    return true;
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return enchantmentLevel * 25;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return this.getMinEnchantability(enchantmentLevel) + 50;
  }

  // TODO move out
  public void onAttachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    ItemStack stack = event.getObject();
    Item item = stack.getItem();

    if (!(item instanceof BlockItem)) {
      return;
    }

    BlockItem blockItem = (BlockItem) item;
    if (!(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
      return;
    }

    event.addCapability(
      new ResourceLocation(Mod.MOD_ID, "shulker_box_item_handler_value"),
      new ItemStackHelperItemHandlerProvider(stack)
    );
  }

  public void onItemPickup(EntityItemPickupEvent event) {
    if (event.isCanceled() || event.getResult() == Event.Result.ALLOW) {
      return;
    }

    ItemEntity itemEntity = event.getItem();
    ItemStack pickedItemStack = itemEntity.getItem();

    for (ItemStack invStack : ModInventoryHelper.getInventoryItems(event.getPlayer())) {
      if (pickedItemStack.isEmpty()) {
        break;
      }

      if (invStack.isEmpty()) {
        continue;
      }

      if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SIPHON, invStack) == 0) {
        continue;
      }

      Optional<IItemHandler> itemHandler = invStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
      if (!itemHandler.isPresent()) {
        continue;
      }

      IItemHandler resolvedItemHandler = itemHandler.get();

      if (hasItem(resolvedItemHandler, pickedItemStack)) {
        pickedItemStack = ItemHandlerHelper.insertItemStacked(resolvedItemHandler, pickedItemStack.copy(), false);
      }
    }

    int totalPickedUp = itemEntity.getItem().getCount() - pickedItemStack.getCount();

    if (totalPickedUp > 0) {
      event.setCanceled(true);
      itemEntity.getItem().setCount(pickedItemStack.getCount());
      event.getPlayer().inventory.markDirty();

      if (!event.getItem().isSilent()) {
        event.getItem().world.playSound(null, event.getPlayer().getPosX(), event.getPlayer().getPosY(), event.getPlayer().getPosZ(),
          SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
          ((event.getItem().world.rand.nextFloat() - event.getItem().world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
      }
      ((ServerPlayerEntity) event.getPlayer()).connection.sendPacket(new SCollectItemPacket(event.getItem().getEntityId(), event.getPlayer().getEntityId(), totalPickedUp));

      event.getPlayer().openContainer.detectAndSendChanges();
    }
  }

  public static boolean hasItem(IItemHandler itemHandler, ItemStack itemStack) {
    for (int i = 0; i < itemHandler.getSlots(); i++) {
      if (ItemHandlerHelper.canItemStacksStackRelaxed(itemHandler.getStackInSlot(i), itemStack)) {
        return true;
      }
    }

    return false;
  }
}
