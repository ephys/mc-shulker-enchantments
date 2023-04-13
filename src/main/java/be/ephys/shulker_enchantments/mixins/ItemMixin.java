package be.ephys.shulker_enchantments.mixins;

import be.ephys.shulker_enchantments.ModEnchantments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
  @Inject(method = "isEnchantable", at = @At("RETURN"), cancellable = true)
  private void isEnchantable(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
    if (cir.getReturnValue()) {
      return;
    }

    if (ModEnchantments.REFILL.canEnchant(itemStack) || ModEnchantments.SIPHON.canEnchant(itemStack)) {
      cir.setReturnValue(true);
    }
  }
}
