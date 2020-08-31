package tfar.uncraftingtable.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.uncraftingtable.UncraftingTableBlockEntity;
import tfar.uncraftingtable.UncraftingTableMenu;

import java.util.function.Supplier;

public class C2SLockRecipe {


  public C2SLockRecipe() {
  }

  //decode
  public C2SLockRecipe(PacketBuffer buf) {
  }

  public void encode(PacketBuffer buf) {
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = ctx.get().getSender();
    ctx.get().enqueueWork(() -> {
      if (player.openContainer instanceof UncraftingTableMenu) {
        ((UncraftingTableMenu)player.openContainer).callable.apply(World::getTileEntity).filter(UncraftingTableBlockEntity.class::isInstance)
                .map(UncraftingTableBlockEntity.class::cast).ifPresent(UncraftingTableBlockEntity::lock);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}

