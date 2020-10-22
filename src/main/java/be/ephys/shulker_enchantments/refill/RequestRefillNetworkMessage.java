package be.ephys.shulker_enchantments.refill;

import be.ephys.shulker_enchantments.core.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestRefillNetworkMessage {
  // TODO off-hand
  public final byte inventorySlot; // 0 - 9 (hotbar)
  public final ItemStack itemStackTemplate;
  public final int requestedAmount;

  public RequestRefillNetworkMessage(byte inventorySlot, ItemStack itemStackTemplate, int requestedAmount) {
    this.inventorySlot = inventorySlot;
    this.itemStackTemplate = itemStackTemplate;
    this.requestedAmount = requestedAmount;
  }

  public static void registerPacket() {
    ModNetworking.INSTANCE.registerMessage(
      ModNetworking.REQUEST_REFILL_DISCRIMINATOR,
      RequestRefillNetworkMessage.class,
      RequestRefillNetworkMessage::encode,
      RequestRefillNetworkMessage::decode,
      RequestRefillNetworkMessage::handle
    );
  }

  public static void encode(RequestRefillNetworkMessage message, PacketBuffer buffer) {
    buffer.writeByte(message.inventorySlot);
    buffer.writeItemStack(message.itemStackTemplate);
    buffer.writeInt(message.requestedAmount);
  }

  public static RequestRefillNetworkMessage decode(PacketBuffer buffer) {
    return new RequestRefillNetworkMessage(
      buffer.readByte(),
      buffer.readItemStack(),
      buffer.readInt()
    );
  }

  public static void handle(final RequestRefillNetworkMessage packet, Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      PlayerEntity player = context.get().getSender();

      if (player == null) {
        return;
      }

      RefillHandler.attemptRefill(player, packet.inventorySlot, packet.itemStackTemplate, packet.requestedAmount);
    });

    context.get().setPacketHandled(true);
  }
}
