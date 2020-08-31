package tfar.uncraftingtable.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import tfar.uncraftingtable.ExampleMod;

public class PacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ExampleMod.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, C2SChangeRecipe.class,
            C2SChangeRecipe::encode,
            C2SChangeRecipe::new,
            C2SChangeRecipe::handle);

    INSTANCE.registerMessage(id++, C2SLockRecipe.class,
            C2SLockRecipe::encode,
            C2SLockRecipe::new,
            C2SLockRecipe::handle);
  }
}
