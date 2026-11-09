package jp.halofriends.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Subtle texture-based corona around a real 3D silhouette.
 * Both sides are rendered in HEAD space, never in camera/billboard space.
 * The halo is full-bright but intentionally does NOT illuminate the world.
 */
final class HaloGlow {
    private static final float FRONT_Z = 0.53f;
    private static final float BACK_Z = -0.53f;

    private HaloGlow() { }

    static void render(PoseStack pose, VertexConsumer consumer) {
        // Front quad (+Z normal), back quad (-Z normal).
        vertex(pose, consumer, -0.5f, -0.5f, FRONT_Z, 0.0f, 1.0f, 1.0f);
        vertex(pose, consumer,  0.5f, -0.5f, FRONT_Z, 1.0f, 1.0f, 1.0f);
        vertex(pose, consumer,  0.5f,  0.5f, FRONT_Z, 1.0f, 0.0f, 1.0f);
        vertex(pose, consumer, -0.5f,  0.5f, FRONT_Z, 0.0f, 0.0f, 1.0f);

        vertex(pose, consumer,  0.5f, -0.5f, BACK_Z, 1.0f, 1.0f, -1.0f);
        vertex(pose, consumer, -0.5f, -0.5f, BACK_Z, 0.0f, 1.0f, -1.0f);
        vertex(pose, consumer, -0.5f,  0.5f, BACK_Z, 0.0f, 0.0f, -1.0f);
        vertex(pose, consumer,  0.5f,  0.5f, BACK_Z, 1.0f, 0.0f, -1.0f);
    }

    private static void vertex(PoseStack pose, VertexConsumer consumer,
                               float x, float y, float z, float u, float v, float normalZ) {
        consumer.vertex(pose.last().pose(), x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.last().normal(), 0.0f, 0.0f, normalZ)
                .endVertex();
    }
}
