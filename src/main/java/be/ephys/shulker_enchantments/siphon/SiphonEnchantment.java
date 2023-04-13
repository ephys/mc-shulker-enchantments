package be.ephys.shulker_enchantments.siphon;

import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.Tags;
import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.helpers.ModInventoryHelper;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SiphonEnchantment extends Enchantment {
  public SiphonEnchantment() {
    super(Rarity.RARE, ModEnchantments.SHULKER_LIKE, new EquipmentSlot[0]);
    setRegistryName(Mod.MOD_ID + ":siphon");
    this.descriptionId = "enchantment." + Mod.MOD_ID + ".siphon";
  }

  // TODO: put isEnderChest behind config option
  @Override
  public boolean canEnchant(ItemStack stack) {
    return Tags.isShulkerLike(stack) || Tags.isEnderChest(stack);
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

  public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
    if (event.isCanceled()) {
      return;
    }

    if (!(event.getEntityLiving() instanceof Player player)) {
      return;
    }

    ItemStack sourceStack = event.getItem();
    ItemStack resultStack = event.getResultStack();

    // consuming did not produce a new item (such as an empty bottle)
    if (sourceStack == resultStack) {
      return;
    }

    int totalPickedUp = siphonItem(player, resultStack);
    if (totalPickedUp > 0) {
      resultStack.setCount(resultStack.getCount() - totalPickedUp);
      player.getInventory().setChanged();
    }
  }

  public void onItemPickup(EntityItemPickupEvent event) {
    if (event.isCanceled() || event.getResult() == Event.Result.ALLOW) {
      return;
    }

    ItemEntity itemEntity = event.getItem();
    ItemStack pickedItemStack = itemEntity.getItem();
    Player player = event.getPlayer();

    int totalPickedUp = siphonItem(player, pickedItemStack);

    if (totalPickedUp > 0) {
      event.setCanceled(true);
      pickedItemStack.setCount(pickedItemStack.getCount() - totalPickedUp);
      player.getInventory().setChanged();

      if (!itemEntity.isSilent()) {
        itemEntity.level.playSound(null, player.getX(), player.getY(), player.getZ(),
          SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
          ((itemEntity.level.random.nextFloat() - itemEntity.level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
      }
      ((ServerPlayer) player).connection.send(new ClientboundTakeItemEntityPacket(event.getItem().getId(), event.getPlayer().getId(), totalPickedUp));

      player.containerMenu.broadcastChanges();
    }
  }

  public int siphonItem(Player player, ItemStack pickedItemStack) {
    ItemStack initialItemStack = pickedItemStack;

    for (ItemStack invStack : ModInventoryHelper.getInventoryItems(player)) {
      if (pickedItemStack.isEmpty()) {
        break;
      }

      if (invStack.isEmpty()) {
        continue;
      }

      if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SIPHON, invStack) == 0) {
        continue;
      }

      IItemHandler itemHandler;
      if (Tags.isEnderChest(invStack)) {
        itemHandler = new InvWrapper(player.getEnderChestInventory());
      } else {
        Optional<IItemHandler> optionalItemHandler = invStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
        if (optionalItemHandler.isEmpty()) {
          Mod.LOG.error("Item " + invStack.getItem().getRegistryName() + " is enchanted with siphon but does not have an item handler");
          continue;
        }

        itemHandler = optionalItemHandler.get();
      }

      if (hasItem(itemHandler, pickedItemStack)) {
        pickedItemStack = pickedItemStack.copy();
        pickedItemStack = addStackToExistingStacksOnly(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get(), pickedItemStack, false);
        if (!pickedItemStack.isEmpty()) {
          pickedItemStack = ItemHandlerHelper.insertItemStacked(itemHandler, pickedItemStack, false);
        }
      }
    }

    int totalPickedUp = initialItemStack.getCount() - pickedItemStack.getCount();

    return totalPickedUp;
  }

  public static ItemStack addStackToExistingStacksOnly(IItemHandler inventory, @Nonnull ItemStack stack, boolean simulate) {
    for (int i = 0; i < inventory.getSlots(); i++) {
      if (!ItemHandlerHelper.canItemStacksStackRelaxed(inventory.getStackInSlot(i), stack)) {
        continue;
      }

      stack = inventory.insertItem(i, stack, simulate);

      if (stack.isEmpty()) {
        break;
      }
    }

    return stack;
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
