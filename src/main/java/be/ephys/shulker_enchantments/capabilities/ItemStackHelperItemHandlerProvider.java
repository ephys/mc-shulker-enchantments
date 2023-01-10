package be.ephys.shulker_enchantments.capabilities;

import be.ephys.shulker_enchantments.core.Mod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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

  public static void onAttachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    ItemStack stack = event.getObject();
    Item item = stack.getItem();

    if (!(item instanceof BlockItem)) {
      return;
    }

    BlockItem blockItem = (BlockItem) item;
    if (!(blockItem.getBlock() instanceof ShulkerBoxBlock) && !(blockItem.getBlock() instanceof EnderChestBlock)) {
      return;
    }

    event.addCapability(
      new ResourceLocation(Mod.MOD_ID, "shulker_box_item_handler_value"),
      new ItemStackHelperItemHandlerProvider(stack)
    );
  }
}
