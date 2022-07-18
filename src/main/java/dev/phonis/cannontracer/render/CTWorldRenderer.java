package dev.phonis.cannontracer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.cannontracer.state.CTLineManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public
class CTWorldRenderer
{

    public static
    void drawLines(MatrixStack matrixStack)
    {
        Matrix4f      matrix        = matrixStack.peek().getPositionMatrix();
        Camera        camera        = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d         camPos        = camera.getPos();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        CTLineManager.instance.forEachLineInCurrentWorld( // iterates through lines in current world after getting lock
            line ->
            {
                float red     = line.getR();
                float green   = line.getG();
                float blue    = line.getB();
                float alpha   = 1F;
                Vec3d start   = new Vec3d(line.start.x, line.start.y, line.start.z);
                Vec3d end     = new Vec3d(line.finish.x, line.finish.y, line.finish.z);
                Vec3d toStart = start.subtract(camPos);
                Vec3d toEnd   = end.subtract(camPos);
                float x1      = (float) toStart.x;
                float y1      = (float) toStart.y;
                float z1      = (float) toStart.z;
                float x2      = (float) toEnd.x;
                float y2      = (float) toEnd.y;
                float z2      = (float) toEnd.z;
                bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
                BufferRenderer.drawWithShader(bufferBuilder.end());
            });
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

}
