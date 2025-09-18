package be.ephys.shulker_enchantments.core;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import be.ephys.shulker_enchantments.CopyEnchantmentsLootModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@net.minecraftforge.fml.common.Mod(Mod.MOD_ID)
public class Mod {
  public static final String MOD_ID = "shulker_enchantments";
  public static final Logger LOG = LogManager.getLogger(MOD_ID);
  private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Mod.MOD_ID);

  public static final String PERSISTED_ITEM_NBT_TAG_ID = Mod.MOD_ID + ":PersistedItemNbt";

  static {
    GLM.register("copy_enchantments", CopyEnchantmentsLootModifier.CODEC);
  }

  public Mod(FMLJavaModLoadingContext context) {
    ConfigSynchronizer.synchronizeConfig();

    GLM.register(context.getModEventBus());
  }
}
