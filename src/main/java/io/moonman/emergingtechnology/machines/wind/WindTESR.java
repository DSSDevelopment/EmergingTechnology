package io.moonman.emergingtechnology.machines.wind;

import io.moonman.emergingtechnology.EmergingTechnology;
import io.moonman.emergingtechnology.helpers.machines.enums.TurbineSpeedEnum;
import io.moonman.emergingtechnology.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Resource;

public class WindTESR extends TileEntitySpecialRenderer<WindTileEntity> {
    @Override
    public void render(WindTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!te.getHasTurbine()) { return; }
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Translate to the location of our tile entity
        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        renderBlade(te);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderBlade(WindTileEntity te) {
        GlStateManager.translate(0.5, 0.5, 0.25);
        long speed = te.getTurbineState() == TurbineSpeedEnum.SLOW ? (long)11.0 : (long)7.0;
        long angle = (System.currentTimeMillis() / speed) % 360;
        GlStateManager.rotate(angle, 0, 0, (float) 0.5);

        //RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        World world = te.getWorld();
        // Translate back to local view coordinates so that we can do the actual rendering here
        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IModel model;
        try {
            model = OBJLoader.INSTANCE.loadModel(new ResourceLocation(EmergingTechnology.MODID, "models/item/windmill_blade_t1.obj"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        IBakedModel bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
                world,
                bakedModel,
                world.getBlockState(te.getPos()),
                te.getPos(),
                Tessellator.getInstance().getBuffer(), false);
        tessellator.draw();

        //RenderHelper.enableStandardItemLighting();
    }
}
