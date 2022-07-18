package dev.phonis.cannontracer.mixin;

import dev.phonis.cannontracer.render.CTWorldRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract
class MixinGameRenderer
{

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
                     opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void onWorldRender(float tickDelta, long limitTime, MatrixStack matrixStack, CallbackInfo ci)
    {
        CTWorldRenderer.drawLines(matrixStack);
    }

}
