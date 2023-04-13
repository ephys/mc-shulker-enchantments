package be.ephys.shulker_enchantments.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * IItemHandler for Quark's seed pouch
 */
public class SeedPouchItemHandler extends ItemStackHandler {
  private final ItemStack stack;

  public SeedPouchItemHandler(ItemStack stack) {
    super(10);
    this.stack = stack;
    this.deserializeNBT(this.stack.getTag());
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag nbt = new CompoundTag();

  writeNBT(nbt);

    return nbt;
  }

  private void writeNBT(CompoundTag nbt) {
    ItemStack storedItem = ItemStack.EMPTY;
    int itemCount = 0;

    for (int i = 0; i < 10; i++) {
      ItemStack stack = stacks.get(i);

      if (stack.isEmpty()) {
        continue;
      }

      if (storedItem.isEmpty()) {
        storedItem = stack;
      }

      itemCount += stack.getCount();
    }

    if (itemCount == 0) {
      nbt.remove("storedItem");
      nbt.remove("itemCount");
    } else {
      CompoundTag storedItemNbt = storedItem.save(new CompoundTag());
      storedItemNbt.putInt("Count", 1);
      nbt.put("storedItem", storedItemNbt);
      nbt.putInt("itemCount", itemCount);
    }

  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    if (nbt == null) {
      return;
    }

    if (!nbt.contains("storedItem")) {
      this.setSize(10);
      return;
    }

    CompoundTag storedItemNbt = nbt.getCompound("storedItem");
    ItemStack storedItem = ItemStack.of(storedItemNbt);
    int itemCount = nbt.getInt("itemCount");

    int fullStackCount = itemCount / 64;
    int lastStackContents = itemCount % 64;

    for (int i = 0; i < fullStackCount; i++) {
      ItemStack stack = storedItem.copy();
      stack.setCount(64);
      stacks.set(i, stack);
    }

    if (fullStackCount < 10) {
      if (lastStackContents == 0) {
        stacks.set(fullStackCount, ItemStack.EMPTY);
      } else {
        ItemStack stack = storedItem.copy();
        stack.setCount(lastStackContents);
        stacks.set(fullStackCount, stack);
      }

      for (int i = fullStackCount + 1; i < 10; i++) {
        stacks.set(i, ItemStack.EMPTY);
      }
    }

    onLoad();
  }

  protected void onContentsChanged(int slot) {
    CompoundTag nbt = stack.getOrCreateTag();
    writeNBT(nbt);

    int itemCount = nbt.getInt("itemCount");
    if (itemCount == 0) {
      stack.setDamageValue(0);
    } else {
      stack.setDamageValue(641 - itemCount);
    }
  }
}
