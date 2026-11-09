package jp.halofriends.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Soft full-bright aura around a real 3D silhouette.
 * Both sides are rendered in HEAD space, never in camera/billboard space.
 */
final class HaloGlow {
    private HaloGlow() { }

    static void render(PoseStack pose, VertexConsumer consumer) {
        render(pose, consumer, 1.0f, 0.56f);
    }

    static void render(PoseStack pose, VertexConsumer consumer, float alphaMul, float zExtent) {
        int alpha = Math.max(0, Math.min(255, Math.round(255.0f * alphaMul)));
        // Front quad (+Z normal), back quad (-Z normal).
        vertex(pose, consumer, -0.5f, -0.5f,  zExtent, 0.0f, 1.0f, 1.0f, alpha);
        vertex(pose, consumer,  0.5f, -0.5f,  zExtent, 1.0f, 1.0f, 1.0f, alpha);
        vertex(pose, consumer,  0.5f,  0.5f,  zExtent, 1.0f, 0.0f, 1.0f, alpha);
        vertex(pose, consumer, -0.5f,  0.5f,  zExtent, 0.0f, 0.0f, 1.0f, alpha);

        vertex(pose, consumer,  0.5f, -0.5f, -zExtent, 1.0f, 1.0f, -1.0f, alpha);
        vertex(pose, consumer, -0.5f, -0.5f, -zExtent, 0.0f, 1.0f, -1.0f, alpha);
        vertex(pose, consumer, -0.5f,  0.5f, -zExtent, 0.0f, 0.0f, -1.0f, alpha);
        vertex(pose, consumer,  0.5f,  0.5f, -zExtent, 1.0f, 0.0f, -1.0f, alpha);
    }

    private static void vertex(PoseStack pose, VertexConsumer consumer,
                               float x, float y, float z, float u, float v,
                               float normalZ, int alpha) {
        consumer.vertex(pose.last().pose(), x, y, z)
                .color(255, 255, 255, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.last().normal(), 0.0f, 0.0f, normalZ)
                .endVertex();
    }
}
