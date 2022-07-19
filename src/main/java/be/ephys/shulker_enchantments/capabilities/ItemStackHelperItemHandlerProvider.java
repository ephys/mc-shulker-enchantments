package be.ephys.shulker_enchantments.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemStackHelperItemHandlerProvider implements ICapabilityProvider {
  private final ItemStack shulkerBoxItemStack;
  private LazyOptional<IItemHandler> inventoryHandler;

  public ItemStackHelperItemHandlerProvider(ItemStack shulkerBoxItemStack) {
    this.shulkerBoxItemStack = shulkerBoxItemStack;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      if (this.inventoryHandler == null) {
        this.inventoryHandler = LazyOptional.of(this::createHandler);
      }

      return this.inventoryHandler.cast();
    }

    return LazyOptional.empty();
  }

  private IItemHandler createHandler() {
    return new ItemStackHelperItemHandler(shulkerBoxItemStack, 27, 64);
  }
}
