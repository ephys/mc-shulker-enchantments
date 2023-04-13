package be.ephys.shulker_enchantments.refill;

import be.ephys.cookiecore.config.Config;
import be.ephys.shulker_enchantments.Tags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class RefillConfig {
  @Config(name = "refill_offhand", side = ModConfig.Type.SERVER, description = "Refill the offhand slot")
  @Config.BooleanDefault(value = true)
  public static ForgeConfigSpec.BooleanValue refillOffhand;

  @Config(name = "refill_scope", side = ModConfig.Type.SERVER, description = """
Which parts of the inventory are refilled:
- HAND: Only active hand (+ offhand if refill_offhand is true),
- HOTBAR: The entire hotbar (+ offhand if refill_offhand is true),
- INVENTORY: The entire 36-slot inventory (+ offhand if refill_offhand is true)""")
  @Config.EnumDefault(value = "HOTBAR", enumType = RefillScope.class)
  public static ForgeConfigSpec.EnumValue<RefillScope> refillScope;

  @Config(name = "refill_non_stackables", side = ModConfig.Type.SERVER, description = "Refill non-stackable items (like the totem of undying). You can also ban specific items by adding them to the shulker_enchantments:non_refillable item tag.")
  @Config.BooleanDefault(value = false)
  public static ForgeConfigSpec.BooleanValue refillNonStackables;

  enum RefillScope {
    HAND,
    HOTBAR,
    INVENTORY,
  }

  public static boolean canBeRefilled(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }

    if (stack.is(Tags.TAG_NON_REFILLABLE)) {
      return false;
    }

    if (!RefillConfig.refillNonStackables.get() && !stack.isStackable()) {
      return false;
    }

    return true;
  }
}
