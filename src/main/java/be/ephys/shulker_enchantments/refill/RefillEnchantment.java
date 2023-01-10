package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.core.Mod;
import be.ephys.shulker_enchantments.Tags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * This is merely the enchantment.
 *
 * The actual refill logic starts in {@link RefillClientEvents#onPlayerTick}
 * The client checks for item use, and sends a refill-request packet to the server when necessary.
 * The server then refills the item on packet reception.
 */
public class RefillEnchantment extends Enchantment {
  public RefillEnchantment() {
    super(Rarity.RARE, ModEnchantments.SHULKER_LIKE, new EquipmentSlot[0]);
    setRegistryName(Mod.MOD_ID + ":refill");
    this.descriptionId = "enchantment." + Mod.MOD_ID + ".refill";
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    return Tags.isShulkerLike(stack);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isTreasureOnly() {
    return true;
  }

  @Override
  public int getMinCost(int enchantmentLevel) {
    return enchantmentLevel * 25;
  }

  @Override
  public int getMaxCost(int enchantmentLevel) {
    return this.getMinCost(enchantmentLevel) + 50;
  }
}
