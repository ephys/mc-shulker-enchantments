package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.core.ModNetworking;
import be.ephys.shulker_enchantments.helpers.ModInventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

@OnlyIn(Dist.CLIENT)
public class RefillClientEvents {
  private static final InventoryData previousInv = new InventoryData();

  /**
   * Checks the inventory to detect usage of items & blocks.
   * Requests server to refill when a detection occurs.
   */
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side != LogicalSide.CLIENT) {
      return;
    }

    if (Minecraft.getInstance().screen != null) {
      // this invalidates the current inventory cache
      // to prevent the hotbar from filling up the second the interface is closed
      previousInv.selectedHotbarSlot = -1;
      return;
    }

    final Player player = event.player;
    final ItemStack currentItemStack = event.player.getInventory().getSelected();
    final int hotbarSlot = player.getInventory().selected;

    boolean wasEmptied = previousInv.currentStackSize > 0 && currentItemStack.isEmpty();

    // detect whether player changed which hotbar slot is selected
    // or replaced current ItemStack in the active hotbar slot
    if (hotbarSlot != previousInv.selectedHotbarSlot || (
      !wasEmptied && !ModInventoryHelper.areItemStacksEqual(currentItemStack, previousInv.currentItemStack))
    ) {
      previousInv.selectedHotbarSlot = hotbarSlot;
      previousInv.currentItemStack = currentItemStack.copy();
      previousInv.currentStackSize = currentItemStack.getCount();

      return;
    }

    int newStackSize = currentItemStack.getCount();
    if (newStackSize < previousInv.currentStackSize) {
      RefillClientEvents.requestRefill((byte) hotbarSlot, previousInv.currentItemStack, previousInv.currentStackSize - newStackSize);
    }

    previousInv.currentStackSize = newStackSize;
  }

  private static void requestRefill(byte inventorySlot, ItemStack itemStackTemplate, int requestedAmount) {
    ModNetworking.INSTANCE.sendToServer(new RequestRefillNetworkMessage(inventorySlot, itemStackTemplate, requestedAmount));
  }

  private static class InventoryData {
    private int selectedHotbarSlot = -1;
    private ItemStack currentItemStack = ItemStack.EMPTY;
    private int currentStackSize = 0;
  }
}
