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
 * REAL 3D halo layer: pre-triangulated colored faces AND actual extruded side walls.
 * Unlike v0.3, the model is fixed in player/head space: never camera-billboarded.
 * Does not add dynamic light to surrounding blocks (full-bright vertex shading only).
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
            HaloMesh3D model;
            float width, height, thickness, tilt;
            double elevation;

            if (helmet.is(HaloFriends.SHIROKO.get())) {
                texture = SHIROKO_TEXTURE;
                model = SHIROKO_MESH;
                width = 1.03f; height = 0.78f; thickness = 0.085f;
                tilt = 60.0f; elevation = -0.92d;
            } else if (helmet.is(HaloFriends.HINA.get())) {
                texture = HINA_TEXTURE;
                model = HINA_MESH;
                width = 1.34f; height = 0.87f; thickness = 0.10f;
                tilt = 57.0f; elevation = -0.94d;
            } else if (helmet.is(HaloFriends.WAKAMO.get())) {
                texture = WAKAMO_TEXTURE;
                model = WAKAMO_MESH;
                width = 0.90f; height = 0.96f; thickness = 0.08f;
                tilt = 35.0f; elevation = -1.02d;
            } else {
                return;
            }

            pose.pushPose();
            getParentModel().head.translateAndRotate(pose);
            pose.translate(0.0d, elevation, 0.0d);
            // Tilt the REAL solid model above the head. Player rotation, NOT camera rotation.
            pose.mulPose(Axis.XP.rotationDegrees(tilt));
            pose.scale(width, height, thickness);

            VertexConsumer surfaces = buffers.getBuffer(RenderType.entityTranslucent(texture));
            model.renderFrontBack(pose, surfaces);
            VertexConsumer sides = buffers.getBuffer(RenderType.entityTranslucent(WHITE_TEXTURE));
            model.renderWalls(pose, sides);
            pose.popPose();
        }
    }
}
