package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.helpers.ModInventoryHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class RefillHandler {
  public static void attemptRefill(final PlayerEntity player, final int hotbarSlot, final ItemStack itemTemplate, int requestedAmount) {
    if (player.isSpectator()) {
      return;
    }

    ItemStack hotbarStack = player.inventory.getStackInSlot(hotbarSlot);

    // clamp to max stack size to ensure bad packets don't cause it to go above the max stack size
    requestedAmount = Math.min(requestedAmount, itemTemplate.getMaxStackSize() - hotbarStack.getCount());

    // this can happen if the user changed the stack before the
    // refill-request packet could arrive
    if (!hotbarStack.isEmpty() && !ModInventoryHelper.areItemStacksEqual(itemTemplate, hotbarStack)) {
      return;
    }

    int foundAmount = 0;
    for (ItemStack invStack : player.inventory.mainInventory) {
      if (foundAmount >= requestedAmount) {
        break;
      }

      if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.REFILL, invStack) == 0) {
        continue;
      }

      LazyOptional<IItemHandler> itemHandler = invStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
      if (!itemHandler.isPresent()) {
        continue;
      }

      IItemHandler resolvedItemHandler = itemHandler.resolve().get();

      // TODO: leave 1 item in chest if the chest has SiphonEnchantment on it
      int missingAmount = requestedAmount - foundAmount;
      ItemStack extractedStack = extractItem(resolvedItemHandler, itemTemplate, missingAmount);
      foundAmount += extractedStack.getCount();
    }

    if (foundAmount == 0) {
      return;
    }

    ItemStack newStack;
    if (hotbarStack.isEmpty()) {
      newStack = itemTemplate.copy();
      newStack.setCount(foundAmount);
    } else {
      newStack = hotbarStack.copy();
      newStack.setCount(hotbarStack.getCount() + foundAmount);
    }

    // there is some weird client desync is happening here:
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
    player.openContainer.detectAndSendChanges();
    player.replaceItemInInventory(hotbarSlot, newStack);
    player.openContainer.detectAndSendChanges();
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
