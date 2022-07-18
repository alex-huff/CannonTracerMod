package dev.phonis.cannontracer.render;

import dev.phonis.cannontracer.networking.CTVec3;
import dev.phonis.cannontracer.state.CTLineManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public
class CTWorldRenderer
{

    public static
    void drawLines(float partialTicks, MatrixStack matrixStack)
    {
        MinecraftClient    minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity player          = minecraftClient.player;
        if (player == null) return;
        double             posX            = player.prevX + (player.getX() - player.prevX) * partialTicks;
        double             posY            = player.prevY + (player.getY() - player.prevY) * partialTicks;
        double             posZ            = player.prevZ + (player.getZ() - player.prevZ) * partialTicks;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1f);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glTranslated(-posX, -posY, -posZ);
        CTLineManager.instance.forEachLineInCurrentWorld( // iterates through lines in current world after getting lock
            line ->
            {
                if (line.start.x == line.finish.x && line.start.z == line.finish.z && CTWorldRenderer.in1x1(line.start))
                {
                    GL11.glColor4f(.3f, .3f, .3f, 1f);
                }
                else
                {
                    GL11.glColor4f(line.getR(), line.getG(), line.getB(), 1f);
                }
                GL11.glBegin(0x3);
                GL11.glVertex3d(line.start.x, line.start.y, line.start.z);
                GL11.glVertex3d(line.finish.x, line.finish.y, line.finish.z);
                GL11.glEnd();
            });
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glLineWidth(1f);
        GL11.glTranslated(0, 0, 0);
        GL11.glPopMatrix();
    }

    private static
    boolean in1x1(CTVec3 point)
    {
        double xRemainder = Math.abs(point.x % 1d);
        double zRemainder = Math.abs(point.z % 1d);

        return xRemainder >= .4899d && xRemainder <= .5101d && zRemainder >= .4899d && zRemainder <= .5101d;
    }

}
