package tfar.uncraftingtable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import tfar.uncraftingtable.network.C2SChangeRecipe;
import tfar.uncraftingtable.network.C2SLockRecipe;
import tfar.uncraftingtable.network.PacketHandler;

public class UncraftingTableScreen extends ContainerScreen<UncraftingTableMenu> {

	private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

	public UncraftingTableScreen(UncraftingTableMenu screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	public LockIconButton lockIconButton;

	@Override
	protected void init() {
		super.init();
		addButton(new SmallButton( guiLeft + 95, guiTop + 4,10,10,new StringTextComponent("<"), b -> onPress(b,false)));
		addButton(new SmallButton( guiLeft + 135, guiTop + 4,10,10,new StringTextComponent(">"),b -> onPress(b,true)));
		lockIconButton = new LockIconButton( guiLeft + 153, guiTop + 35,this::onClickLock);
		lockIconButton.setLocked(UncraftingManager.locked);
		addButton(lockIconButton);
	}

	public void onPress(Button b,boolean bl) {
		PacketHandler.INSTANCE.sendToServer(new C2SChangeRecipe(bl));
	}

	public void onClickLock(Button b) {
		UncraftingManager.locked = !UncraftingManager.locked;
		lockIconButton.setLocked(UncraftingManager.locked);
		PacketHandler.INSTANCE.sendToServer(new C2SLockRecipe());
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.func_230459_a_(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		blit(matrixStack, i-80, j, 0, 0, this.xSize + 80, this.ySize,-256,256);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		super.drawGuiContainerForegroundLayer(matrixStack, x, y);
		font.drawString(matrixStack,"Recipes: "+container.intReferenceHolder.get(),95,72,0x800000);
	}
}
