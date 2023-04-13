package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.core.ModNetworking;
import be.ephys.shulker_enchantments.helpers.ModInventoryHelper;
import be.ephys.shulker_enchantments.refill.RefillConfig.RefillScope;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class RefillClientEvents {
  private static final ItemStack[] previousInventory = new ItemStack[41];
  static {
    Arrays.fill(previousInventory, ItemStack.EMPTY);
  }

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
      return;
    }

    final Player player = event.player;

    if (RefillConfig.refillOffhand.get()) {
      checkRefill(player, Inventory.SLOT_OFFHAND);
    }

    RefillScope refillScope = RefillConfig.refillScope.get();
    if (refillScope == RefillScope.HAND) {
      checkRefill(player, player.getInventory().selected);
    } else {
      int watchedInventorySize = RefillConfig.refillScope.get() == RefillScope.HOTBAR ? 9 : Inventory.INVENTORY_SIZE;
      for (int i = 0; i < watchedInventorySize; i++) {
        checkRefill(player, i);
      }
    }
  }

  private static void checkRefill(Player player, int slot) {
    ItemStack currentStack = player.getInventory().getItem(slot);
    ItemStack previousStack = previousInventory[slot];

    boolean wasEmptied = !previousStack.isEmpty() && currentStack.isEmpty();

    // detect whether player changed which hotbar slot is selected
    // or replaced current ItemStack in the active hotbar slot
    if (!ModInventoryHelper.areItemStacksEqual(currentStack, previousStack)) {
      previousInventory[slot] = currentStack.copy();

      if (!wasEmptied) {
        return;
      }
    }

    int newStackSize = currentStack.getCount();
    int previousStackSize = previousStack.getCount();
    if (newStackSize < previousStackSize && RefillConfig.canBeRefilled(previousStack)) {
      requestRefill((byte) slot, previousStack, previousStackSize - newStackSize);
    }

    previousStack.setCount(newStackSize);
  }

  private static void requestRefill(byte inventorySlot, ItemStack itemStackTemplate, int requestedAmount) {
    ModNetworking.INSTANCE.sendToServer(new RequestRefillNetworkMessage(inventorySlot, itemStackTemplate, requestedAmount));
  }
}
