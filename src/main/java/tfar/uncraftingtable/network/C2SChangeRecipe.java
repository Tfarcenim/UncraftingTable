package tfar.uncraftingtable.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.uncraftingtable.UncraftingTableBlockEntity;
import tfar.uncraftingtable.UncraftingTableMenu;

import java.util.function.Supplier;


public class C2SChangeRecipe {

  boolean right;

  public C2SChangeRecipe() {
  }

  public C2SChangeRecipe(boolean right) {
    this.right = right;
  }

  //decode
  public C2SChangeRecipe(PacketBuffer buf) {
    this.right = buf.readBoolean();
  }

  public void encode(PacketBuffer buf) {
    buf.writeBoolean(right);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = ctx.get().getSender();
    ctx.get().enqueueWork(() -> {
      if (player.openContainer instanceof UncraftingTableMenu) {
        ((UncraftingTableMenu)player.openContainer).callable.apply(World::getTileEntity).filter(UncraftingTableBlockEntity.class::isInstance)
                .map(UncraftingTableBlockEntity.class::cast).ifPresent(uncraftingTableBlockEntity -> uncraftingTableBlockEntity.cycleRecipe(right));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}

