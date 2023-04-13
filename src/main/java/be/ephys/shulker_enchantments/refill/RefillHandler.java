package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.Tags;
import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.helpers.ModInventoryHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Optional;

public class RefillHandler {
  public static void attemptRefill(final Player player, final int inventorySlot, final ItemStack itemTemplate, int requestedAmount) {
    if (player.isSpectator()) {
      return;
    }

    if (!RefillConfig.canBeRefilled(itemTemplate)) {
      return;
    }

    if (inventorySlot == Inventory.SLOT_OFFHAND && !RefillConfig.refillOffhand.get()) {
      return;
    }

    ItemStack currentStack = player.getInventory().getItem(inventorySlot);

    // clamp to max stack size to ensure bad packets don't cause it to go above the max stack size
    requestedAmount = Math.min(requestedAmount, itemTemplate.getMaxStackSize() - currentStack.getCount());

    // this can happen if the user changed the stack before the
    // refill-request packet could arrive
    if (!currentStack.isEmpty() && !ModInventoryHelper.areItemStacksEqual(itemTemplate, currentStack)) {
      return;
    }

    int foundAmount = 0;
    for (ItemStack invStack : ModInventoryHelper.getInventoryItems(player)) {
      if (foundAmount >= requestedAmount) {
        break;
      }

      if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.REFILL, invStack) == 0) {
        continue;
      }

      IItemHandler itemHandler;
      if (Tags.isEnderChest(invStack)) {
        itemHandler = new InvWrapper(player.getEnderChestInventory());
      } else {
        Optional<IItemHandler> optionalItemHandler = invStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
        if (!optionalItemHandler.isPresent()) {
          Mod.LOG.error("Item " + invStack.getItem().getRegistryName() + " is enchanted with refill but does not have an item handler");
          continue;
        }

        itemHandler = optionalItemHandler.get();
      }

      // TODO: leave 1 item in chest if the chest has SiphonEnchantment on it
      int missingAmount = requestedAmount - foundAmount;
      ItemStack extractedStack = extractItem(itemHandler, itemTemplate, missingAmount);
      foundAmount += extractedStack.getCount();
    }

    if (foundAmount == 0) {
      return;
    }

    ItemStack newStack;
    if (currentStack.isEmpty()) {
      newStack = itemTemplate.copy();
      newStack.setCount(foundAmount);
    } else {
      newStack = currentStack.copy();
      newStack.setCount(currentStack.getCount() + foundAmount);
    }

    // there is some weird client desync happening here:
    // trying to give back exactly what was just used causes a desync
    // setting it to anything else does however work.
    //
    // The issue is that detectAndSendChanges uses a variable "inventoryItemStacks" to compare what it last sent
    // But it does not always send changes, such as when placing blocks. (The client updates on its own, optimistically)
    //
    // So what happens is:
    // - Client has 14 sands in slot 0, Server has 14 sands in slot 0, (server)inventoryItemStacks has 14 sands in slot 0
    // - Client places a Block
    // - Client has 13 sands in slot 0, Server has 13 sands in slot 0, inventoryItemStacks has 14 sands in slot 0 (because it doesn't need to send that update)
    // - Client requests refill to sand blocks
    // - Client has 13 sands in slot 0, Server has 14 sands in slot 0, inventoryItemStacks has 14 sands in slot
    // - We call detectAndSendChanges, it compares Server stacks with inventoryItemStacks and it turns out they are the same. No packet is sent.
    //
    // This first detectAndSendChanges before we do any change ensures that the client is synchronised.
    player.containerMenu.broadcastChanges();
    player.getInventory().setItem(inventorySlot, newStack);
    player.containerMenu.broadcastChanges();
  }

  private static ItemStack extractItem(IItemHandler inventory, ItemStack itemTemplate, int requestedAmount) {
    ItemStack output = itemTemplate.copy();
    output.setCount(0);

    for (int i = 0; i < inventory.getSlots(); i++) {
      if (output.getCount() >= requestedAmount) {
        break;
      }

      if (!ItemHandlerHelper.canItemStacksStack(inventory.getStackInSlot(i), itemTemplate)) {
        continue;
      }

      int missingAmount = requestedAmount - output.getCount();
      ItemStack extracted = inventory.extractItem(i, missingAmount, false);
      output.setCount(output.getCount() + extracted.getCount());
    }

    return output;
  }
}
