package be.ephys.shulker_enchantments.core;

import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.refill.RefillClientEvents;
import be.ephys.shulker_enchantments.refill.RequestRefillNetworkMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@net.minecraftforge.fml.common.Mod(Mod.MOD_ID)
public class Mod {
  public static final String MOD_ID = "shulker_enchantments";

  public Mod() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

    FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Enchantment.class, ModEnchantments::registerEnchantments);
  }

  private void commonSetup(final FMLCommonSetupEvent evt) {
    RequestRefillNetworkMessage.registerPacket();
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    MinecraftForge.EVENT_BUS.addListener(RefillClientEvents::onPlayerTick);
  }
}
