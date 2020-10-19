package be.ephys.shulker_enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ShulkerEnchantments.MODID)
public class ShulkerEnchantments {
  public static final String MODID = "shulker_enchantments";

  public ShulkerEnchantments() {
    FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Enchantment.class, ModEnchantments::registerEnchantments);
  }
}
