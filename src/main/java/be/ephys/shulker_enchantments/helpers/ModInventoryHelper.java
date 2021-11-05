package be.ephys.shulker_enchantments.helpers;

import com.google.common.collect.Iterables;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public class ModInventoryHelper {

  public static boolean isCuriosInstalled() {
    return ModList.get().isLoaded("curios");
  }

  public static class EmptyIterator<T> implements Iterator<T> {

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public T next() {
      throw new RuntimeException("Iterator is empty");
    }
  }

  public static class IItemHandlerIterator implements Iterator<ItemStack> {
    private final IItemHandler itemHandler;
    private int nextIndex = 0;

    public IItemHandlerIterator(IItemHandler itemHandler) {
      this.itemHandler = itemHandler;
    }

    @Override
    public boolean hasNext() {
      return this.nextIndex < this.itemHandler.getSlots();
    }

    @Override
    public ItemStack next() {
      return this.itemHandler.getStackInSlot(nextIndex++);
    }
  }

  public static class IItemHandlerIterable implements Iterable<ItemStack> {
    private final IItemHandler itemHandler;

    public IItemHandlerIterable(IItemHandler itemHandler) {
      this.itemHandler = itemHandler;
    }

    @Override
    public Iterator<ItemStack> iterator() {
      return new IItemHandlerIterator(this.itemHandler);
    }
  }

  public static Iterable<ItemStack> getInventoryItems(LivingEntity entity) {
    Iterable<ItemStack> inventoryIterator = null;
    Iterable<ItemStack> curiosIterator = null;

    Optional<IItemHandler> itemHandlerCapability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
    if (itemHandlerCapability.isPresent()) {
      inventoryIterator = new IItemHandlerIterable(itemHandlerCapability.get());
    }

    if (isCuriosInstalled()) {
      Optional<IItemHandlerModifiable> out = CuriosApi.getCuriosHelper().getEquippedCurios(entity).resolve();

      if (out.isPresent()) {
        curiosIterator = new IItemHandlerIterable(out.get());
      }
    }

    if (inventoryIterator == null && curiosIterator == null) {
      return new ArrayList<>();
    }

    if (inventoryIterator == null) {
      return curiosIterator;
    }

    if (curiosIterator == null) {
      return inventoryIterator;
    }

    return Iterables.concat(
      inventoryIterator,
      curiosIterator
    );
  }

  public static ItemStack findInventoryItem(LivingEntity entity, Predicate<ItemStack> predicate) {
    if (isCuriosInstalled()) {
      Optional<ImmutableTriple<String, Integer, ItemStack>> out = CuriosApi.getCuriosHelper().findEquippedCurio(predicate, entity);

      if (out.isPresent()) {
        ImmutableTriple<String, Integer, ItemStack> val = out.get();

        return val.getRight();
      }
    }

    Optional<IItemHandler> itemHandlerCapability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
    if (!itemHandlerCapability.isPresent()) {
      return null;
    }

    IItemHandler itemHandler = itemHandlerCapability.get();

    for (int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack stack = itemHandler.getStackInSlot(i);
      if (predicate.test(stack)) {
        return stack;
      }
    }

    return null;
  }

  public static boolean areItemStacksEqual(ItemStack a, ItemStack b) {
    return (a.isEmpty() && b.isEmpty()) || ItemHandlerHelper.canItemStacksStack(a, b);
  }
}
