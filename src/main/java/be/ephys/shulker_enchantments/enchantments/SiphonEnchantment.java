package be.ephys.shulker_enchantments.enchantments;

import be.ephys.shulker_enchantments.ShulkerEnchantments;
import be.ephys.shulker_enchantments.capabilities.ItemStackHelperItemHandlerProvider;
import be.ephys.shulker_enchantments.ModEnchantments;
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

public class SiphonEnchantment extends Enchantment {
  public SiphonEnchantment() {
    super(Rarity.RARE, ModEnchantments.SHULKER_LIKE, new EquipmentSlotType[0]);
    setRegistryName(ShulkerEnchantments.MODID + ":siphon");
    this.name = "enchantment." + ShulkerEnchantments.MODID + ".siphon";
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public boolean canApply(ItemStack stack) {
    Item item = stack.getItem();

    return item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock;
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isAllowedOnBooks() {
    return true;
  }

  @Override
  public boolean isTreasureEnchantment() {
    return true;
  }

  public int getMinEnchantability(int enchantmentLevel) {
    return enchantmentLevel * 25;
  }

  public int getMaxEnchantability(int enchantmentLevel) {
    return this.getMinEnchantability(enchantmentLevel) + 50;
  }

  // can be found on villager sale
  @Override
  public boolean func_230309_h_() {
    return super.func_230309_h_();
  }

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
      new ResourceLocation(ShulkerEnchantments.MODID, "shulker_box_item_handler_value"),
      new ItemStackHelperItemHandlerProvider(stack)
    );
  }

  public void onItemPickup(EntityItemPickupEvent event) {
    if (event.isCanceled() || event.getResult() == Event.Result.ALLOW) {
      return;
    }

    NonNullList<ItemStack> inventory = event.getPlayer().inventory.mainInventory;
    ItemEntity itemEntity = event.getItem();
    ItemStack pickedItemStack = itemEntity.getItem();

    for (ItemStack itemStack : inventory) {
      if (pickedItemStack.isEmpty()) {
        break;
      }

      ItemStack stack = itemStack;
      if (stack.isEmpty()) {
        continue;
      }

      if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SIPHON, stack) == 0) {
        continue;
      }

      LazyOptional<IItemHandler> itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
      if (!itemHandler.isPresent()) {
        continue;
      }

      IItemHandler resolvedItemHandler = itemHandler.resolve().get();

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
      if (itemHandler.getStackInSlot(i).isItemEqualIgnoreDurability(itemStack)) {
        return true;
      }
    }

    return false;
  }
}
