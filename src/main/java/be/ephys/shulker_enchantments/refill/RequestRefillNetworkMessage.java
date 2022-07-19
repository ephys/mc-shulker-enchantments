package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.core.ModNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @param inventorySlot 0 - 9 (hotbar)
 */
public record RequestRefillNetworkMessage(byte inventorySlot, ItemStack itemStackTemplate, int requestedAmount) {

  // TODO support off-hand

  public static void registerPacket() {
    ModNetworking.INSTANCE.registerMessage(
      ModNetworking.REQUEST_REFILL_DISCRIMINATOR,
      RequestRefillNetworkMessage.class,
      RequestRefillNetworkMessage::encode,
      RequestRefillNetworkMessage::decode,
      RequestRefillNetworkMessage::handle
    );
  }

  public static void encode(RequestRefillNetworkMessage message, FriendlyByteBuf buffer) {
    buffer.writeByte(message.inventorySlot);
    buffer.writeItemStack(message.itemStackTemplate, false);
    buffer.writeInt(message.requestedAmount);
  }

  public static RequestRefillNetworkMessage decode(FriendlyByteBuf buffer) {
    return new RequestRefillNetworkMessage(
      buffer.readByte(),
      buffer.readItem(),
      buffer.readInt()
    );
  }

  public static void handle(final RequestRefillNetworkMessage packet, Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      Player player = context.get().getSender();

      if (player == null) {
        return;
      }

      RefillHandler.attemptRefill(player, packet.inventorySlot, packet.itemStackTemplate, packet.requestedAmount);
    });

    context.get().setPacketHandled(true);
  }
}
