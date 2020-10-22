package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.ShulkerLikeTag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/**
 * This is merely the enchantment.
 *
 * The actual refill logic starts in {@link RefillClientEvents#onPlayerTick}
 * The client checks for item use, and sends a refill-request packet to the server when necessary.
 * The server then refills the item on packet reception.
 */
public class RefillEnchantment extends Enchantment {
  public RefillEnchantment() {
    super(Rarity.RARE, ModEnchantments.SHULKER_LIKE, new EquipmentSlotType[0]);
    setRegistryName(Mod.MOD_ID + ":refill");
    this.name = "enchantment." + Mod.MOD_ID + ".refill";
  }

  @Override
  public boolean canApply(ItemStack stack) {
    return ShulkerLikeTag.isShulkerLike(stack.getItem());
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isTreasureEnchantment() {
    return true;
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return enchantmentLevel * 25;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return this.getMinEnchantability(enchantmentLevel) + 50;
  }
}
