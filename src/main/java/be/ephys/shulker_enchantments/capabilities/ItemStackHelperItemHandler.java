package be.ephys.shulker_enchantments.capabilities;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

/**
 * IItemHandler for ItemStacks that use ItemStackHelper to persist/read their items from their inventories.
 * eg. item dropped when breaking a ShulkerBox.
 */
public class ItemStackHelperItemHandler extends ItemStackHandler {
  private final ItemStack itemStack;
  private final int maxStackSize;

  public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";

  public ItemStackHelperItemHandler(ItemStack itemStack, int slotCount, int maxStackSize) {
    super(slotCount);

    this.itemStack = itemStack;
    this.maxStackSize = maxStackSize;

    CompoundNBT nbt = itemStack.getChildTag(BLOCK_ENTITY_TAG);
    if (nbt != null) {
      this.deserializeNBT(nbt);
    }
  }

  @Override
  public int getSlotLimit(int slot) {
    return this.maxStackSize;
  }

  @Override
  public CompoundNBT serializeNBT() {
    // we override the parent serializeNBT because we need to merge the updated "Items" property with
    // other properties the itemStack has, such as Enchantments

    CompoundNBT nbt = itemStack.getChildTag(BLOCK_ENTITY_TAG);
    if (nbt == null) {
      nbt = new CompoundNBT();
    }

    ItemStackHelper.saveAllItems(nbt, this.stacks, true);

    return nbt;
  }

  protected void onContentsChanged(int slot) {
    CompoundNBT newChildNbt = this.serializeNBT();

    CompoundNBT nbt = itemStack.getTag();
    if (nbt == null) {
      nbt = new CompoundNBT();
    }

    nbt.put(BLOCK_ENTITY_TAG, newChildNbt);
    itemStack.setTag(nbt);
  }
}
