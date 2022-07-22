package be.ephys.shulker_enchantments.siphon;

import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.ShulkerLikeTag;
import be.ephys.shulker_enchantments.capabilities.ItemStackHelperItemHandlerProvider;
import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.helpers.ModInventoryHelper;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;

public class SiphonEnchantment extends Enchantment {
  public SiphonEnchantment() {
    super(Rarity.RARE, ModEnchantments.SHULKER_LIKE, new EquipmentSlot[0]);
    setRegistryName(Mod.MOD_ID + ":siphon");
    this.descriptionId = "enchantment." + Mod.MOD_ID + ".siphon";
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    return ShulkerLikeTag.isShulkerLike(stack);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isTreasureOnly() {
    return true;
  }

  @Override
  public int getMinCost(int enchantmentLevel) {
    return enchantmentLevel * 25;
  }

  @Override
  public int getMaxCost(int enchantmentLevel) {
    return this.getMinCost(enchantmentLevel) + 50;
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

      if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SIPHON, invStack) == 0) {
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
      event.getPlayer().getInventory().setChanged();

      if (!event.getItem().isSilent()) {
        event.getItem().level.playSound(null, event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ(),
          SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
          ((event.getItem().level.random.nextFloat() - event.getItem().level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
      }
      ((ServerPlayer) event.getPlayer()).connection.send(new ClientboundTakeItemEntityPacket(event.getItem().getId(), event.getPlayer().getId(), totalPickedUp));

      event.getPlayer().containerMenu.broadcastChanges();
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
