package jp.halofriends.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import jp.halofriends.HaloFriends;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Front-readable game-style halo: ultra-thin extruded geometry, subtle tilt and emissive overlay.
 * Model follows the player head and never rotates to face the camera.
 * A soft aura is decorative only: it does not light nearby blocks.
 */
@Mod.EventBusSubscriber(modid = HaloFriends.MOD_ID,
        value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class HaloClient {
    private static final ResourceLocation SHIROKO_TEXTURE =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/shiroko.png");
    private static final ResourceLocation HINA_TEXTURE =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/hina.png");
    private static final ResourceLocation WAKAMO_TEXTURE =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/wakamo.png");
    private static final ResourceLocation SHIROKO_GLOW =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/shiroko_glow.png");
    private static final ResourceLocation HINA_GLOW =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/hina_glow.png");
    private static final ResourceLocation WAKAMO_GLOW =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/wakamo_glow.png");
    private static final ResourceLocation WHITE_TEXTURE =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/white.png");

    // This data is loaded client-side from the JAR. Each shape has front/back
    // surfaces and real side walls, including walls around all holes and spikes.
    private static final HaloMesh3D SHIROKO_MESH = HaloMesh3D.load("shiroko");
    private static final HaloMesh3D HINA_MESH = HaloMesh3D.load("hina");
    private static final HaloMesh3D WAKAMO_MESH = HaloMesh3D.load("wakamo");

    private HaloClient() { }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) renderer.addLayer(new HaloLayer(renderer));
        }
    }

    private static final class HaloLayer extends RenderLayer<AbstractClientPlayer,
            PlayerModel<AbstractClientPlayer>> {
        HaloLayer(RenderLayerParent<AbstractClientPlayer,
                PlayerModel<AbstractClientPlayer>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffers, int light,
                           AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                           float partialTick, float age, float headYaw, float headPitch) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            ResourceLocation texture;
            ResourceLocation glowTexture;
            HaloMesh3D model;
            float width, height, thickness, tilt;
            double elevation;

            if (helmet.is(HaloFriends.SHIROKO.get())) {
                texture = SHIROKO_TEXTURE;
                glowTexture = SHIROKO_GLOW;
                model = SHIROKO_MESH;
                width = 1.03f; height = 0.76f; thickness = 0.018f;
                tilt = 18.0f; elevation = -0.96d;
            } else if (helmet.is(HaloFriends.HINA.get())) {
                texture = HINA_TEXTURE;
                glowTexture = HINA_GLOW;
                model = HINA_MESH;
                width = 1.30f; height = 0.85f; thickness = 0.022f;
                tilt = 14.0f; elevation = -1.01d;
            } else if (helmet.is(HaloFriends.WAKAMO.get())) {
                texture = WAKAMO_TEXTURE;
                glowTexture = WAKAMO_GLOW;
                model = WAKAMO_MESH;
                width = 0.96f; height = 0.98f; thickness = 0.020f;
                tilt = 11.0f; elevation = -1.07d;
            } else {
                return;
            }

            pose.pushPose();
            getParentModel().head.translateAndRotate(pose);
            // Close to the top of the head. Avoid the strongly horizontal angles of v0.5.
            // The mesh remains in head space (no camera-facing billboarding).
            pose.translate(0.0d, elevation, -0.03d);
            // Extruded geometry uses local z=-0.5..+0.5, so the scale sets actual thickness in blocks.
            // 11-18 degrees from the vertical plane: front/third-person players
            // can read the original motifs, while the shape has physical side walls.
            // Tilt remains fixed to player/head orientation, never camera-facing.
            pose.mulPose(Axis.XP.rotationDegrees(tilt));
            pose.scale(width, height, thickness);

            VertexConsumer surfaces = buffers.getBuffer(RenderType.entityTranslucent(texture));
            model.renderFrontBack(pose, surfaces);
            VertexConsumer sides = buffers.getBuffer(RenderType.entityTranslucent(WHITE_TEXTURE));
            model.renderWalls(pose, sides);

            // A separate blurred silhouette provides a gentle game-like aura without thick geometry.
            // This is a transparent overlay on BOTH faces, not a camera-facing billboard.
            VertexConsumer glow = buffers.getBuffer(RenderType.entityTranslucent(glowTexture));
            HaloGlow.render(pose, glow);
            pose.popPose();
        }
    }
}
