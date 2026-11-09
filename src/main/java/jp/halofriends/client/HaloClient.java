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
 * Layered 3D halo: keeps the thin game-like profile but adds real depth cues.
 * Each halo now has a darker back plane, a brighter front plane, actual side walls,
 * and a separated glow shell so the result reads as a small floating object rather
 * than a single flat cutout.
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
            float width, height, thickness, tilt, glowScale;
            double elevation;

            if (helmet.is(HaloFriends.SHIROKO.get())) {
                texture = SHIROKO_TEXTURE;
                glowTexture = SHIROKO_GLOW;
                model = SHIROKO_MESH;
                width = 1.05f; height = 0.78f; thickness = 0.060f;
                tilt = 25.0f; elevation = -0.97d; glowScale = 1.05f;
            } else if (helmet.is(HaloFriends.HINA.get())) {
                texture = HINA_TEXTURE;
                glowTexture = HINA_GLOW;
                model = HINA_MESH;
                width = 1.32f; height = 0.86f; thickness = 0.072f;
                tilt = 22.0f; elevation = -1.01d; glowScale = 1.06f;
            } else if (helmet.is(HaloFriends.WAKAMO.get())) {
                texture = WAKAMO_TEXTURE;
                glowTexture = WAKAMO_GLOW;
                model = WAKAMO_MESH;
                width = 0.98f; height = 1.00f; thickness = 0.066f;
                tilt = 18.0f; elevation = -1.06d; glowScale = 1.04f;
            } else {
                return;
            }

            pose.pushPose();
            getParentModel().head.translateAndRotate(pose);
            pose.translate(0.0d, elevation, -0.04d);
            pose.mulPose(Axis.XP.rotationDegrees(tilt));
            pose.scale(width, height, thickness);

            VertexConsumer tex = buffers.getBuffer(RenderType.entityTranslucent(texture));
            VertexConsumer side = buffers.getBuffer(RenderType.entityTranslucent(WHITE_TEXTURE));
            VertexConsumer glow = buffers.getBuffer(RenderType.entityTranslucent(glowTexture));

            // Rear plate: slightly larger and darker. This makes the halo read as a small object
            // with front/back separation instead of a single paper-thin cutout.
            pose.pushPose();
            pose.translate(0.0f, 0.0f, -0.34f);
            pose.scale(1.045f, 1.045f, 1.0f);
            model.renderFrontBack(pose, tex, 0.58f, 0.58f, 0.64f, 0.88f);
            pose.popPose();

            // Real side walls: slightly stronger shading so thickness is readable in motion.
            model.renderWalls(pose, side, 0.92f, 0.92f, 0.96f, 1.0f);

            // Main body.
            model.renderFrontBack(pose, tex);

            // Front sheen: tiny forward offset to create a beveled layered look.
            pose.pushPose();
            pose.translate(0.0f, 0.0f, 0.16f);
            pose.scale(0.992f, 0.992f, 1.0f);
            model.renderFrontBack(pose, tex, 1.0f, 1.0f, 1.0f, 0.62f);
            pose.popPose();

            // Outer glow shell: kept separate from the mesh so the object still reads as thin.
            pose.pushPose();
            pose.scale(glowScale, glowScale, 1.0f);
            HaloGlow.render(pose, glow, 0.75f, 0.68f);
            pose.popPose();

            pose.popPose();
        }
    }
}
