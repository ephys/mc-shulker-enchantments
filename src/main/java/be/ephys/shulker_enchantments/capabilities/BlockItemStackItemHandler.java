package be.ephys.shulker_enchantments.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * IItemHandler for ItemStacks that use ItemStackHelper to persist/read their items from their inventories.
 * eg. item dropped when breaking a ShulkerBox.
 */
public class BlockItemStackItemHandler extends ItemStackHandler {
  private final ItemStack itemStack;
  private final int maxStackSize;

  public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";

  public BlockItemStackItemHandler(ItemStack itemStack, int slotCount, int maxStackSize) {
    super(slotCount);

    this.itemStack = itemStack;
    this.maxStackSize = maxStackSize;

    CompoundTag nbt = itemStack.getTagElement(BLOCK_ENTITY_TAG);
    if (nbt != null) {
      this.deserializeNBT(nbt);
    }
  }

  @Override
  public int getSlotLimit(int slot) {
    return this.maxStackSize;
  }

  @Override
  public CompoundTag serializeNBT() {
    // we override the parent serializeNBT because we need to merge the updated "Items" property with
    // other properties the itemStack has, such as Enchantments

    CompoundTag nbt = itemStack.getTagElement(BLOCK_ENTITY_TAG);
    if (nbt == null) {
      nbt = new CompoundTag();
    }

    ContainerHelper.saveAllItems(nbt, this.stacks, true);

    return nbt;
  }

  protected void onContentsChanged(int slot) {
    CompoundTag newChildNbt = this.serializeNBT();

    CompoundTag nbt = itemStack.getTag();
    if (nbt == null) {
      nbt = new CompoundTag();
    }

    nbt.put(BLOCK_ENTITY_TAG, newChildNbt);
    itemStack.setTag(nbt);
  }
}
