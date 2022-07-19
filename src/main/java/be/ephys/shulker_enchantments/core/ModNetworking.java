package be.ephys.shulker_enchantments.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
    new ResourceLocation(Mod.MOD_ID, "main"),
    () -> PROTOCOL_VERSION,
    PROTOCOL_VERSION::equals,
    PROTOCOL_VERSION::equals
  );

  public static int REQUEST_REFILL_DISCRIMINATOR = 1;
}
