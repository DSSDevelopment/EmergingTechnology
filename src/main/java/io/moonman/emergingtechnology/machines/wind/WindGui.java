package io.moonman.emergingtechnology.machines.wind;

import io.moonman.emergingtechnology.EmergingTechnology;
import io.moonman.emergingtechnology.gui.GuiHelper;
import io.moonman.emergingtechnology.gui.classes.GuiPosition;
import io.moonman.emergingtechnology.init.ModBlocks;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class WindGui extends GuiContainer {
    private static final ResourceLocation TEXTURES = new ResourceLocation(
            EmergingTechnology.MODID + ":textures/gui/cookergui.png");
    private final InventoryPlayer player;
    private final WindTileEntity tileEntity;

    private String NAME = ModBlocks.wind.getLocalizedName();

    private static final int XSIZE = 175;
    private static final int YSIZE = 165;

    // Standard positions for labels
    private static final GuiPosition TOP_LEFT_POS = GuiHelper.getTopLeft();
    private static final GuiPosition TOP_RIGHT_POS = GuiHelper.getTopRight(XSIZE, 44);
    private static final GuiPosition INVENTORY_POS = GuiHelper.getInventory(YSIZE);

    // Draws textures on gui
    public WindGui(InventoryPlayer player, WindTileEntity tileEntity) {
        super(new WindContainer(player, tileEntity));
        this.player = player;
        this.tileEntity = tileEntity;
        this.xSize = XSIZE;
        this.ySize = YSIZE;
    }
    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(TEXTURES);
        this.fontRenderer.drawString(NAME, TOP_LEFT_POS.x, TOP_LEFT_POS.y, GuiHelper.LABEL_COLOUR);
        this.fontRenderer.drawString(GuiHelper.inventoryLabel(this.player), INVENTORY_POS.x, INVENTORY_POS.y,
                GuiHelper.LABEL_COLOUR);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURES);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }


}
