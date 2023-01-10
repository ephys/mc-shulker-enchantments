package be.ephys.shulker_enchantments.core;

import be.ephys.shulker_enchantments.CopyEnchantmentsLootModifier;
import be.ephys.shulker_enchantments.ModEnchantments;
import be.ephys.shulker_enchantments.refill.RefillClientEvents;
import be.ephys.shulker_enchantments.refill.RequestRefillNetworkMessage;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@net.minecraftforge.fml.common.Mod(Mod.MOD_ID)
public class Mod {
  public static final String MOD_ID = "shulker_enchantments";

  private static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MOD_ID);
  private static final RegistryObject<CopyEnchantmentsLootModifier.Serializer> COPY_ENCHANTMENTS = GLM.register("copy_enchantments", CopyEnchantmentsLootModifier.Serializer::new);

  public Mod() {
    GLM.register(FMLJavaModLoadingContext.get().getModEventBus());

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
