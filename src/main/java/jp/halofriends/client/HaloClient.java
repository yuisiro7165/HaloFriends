package jp.halofriends.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import jp.halofriends.HaloFriends;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Renders supplied character-specific RGBA silhouettes above the player's head.
 * Uses full-bright translucent quads (not dynamic light or bloom).
 * No client-side code is referenced by the shared mod class.
 */
@Mod.EventBusSubscriber(modid = HaloFriends.MOD_ID,
        value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class HaloClient {
    private static final ResourceLocation SHIROKO =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/shiroko.png");
    private static final ResourceLocation HINA =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/hina.png");
    private static final ResourceLocation WAKAMO =
            new ResourceLocation(HaloFriends.MOD_ID, "textures/entity/wakamo.png");

    private HaloClient() {}

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
                PlayerModel<AbstractClientPlayer>> parent) { super(parent); }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffers, int light,
                           AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                           float partialTick, float age, float headYaw, float headPitch) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            ResourceLocation texture;
            float width;
            float height;
            if (helmet.is(HaloFriends.SHIROKO.get())) {
                texture = SHIROKO; width = 0.98f; height = 0.80f;
            } else if (helmet.is(HaloFriends.HINA.get())) {
                texture = HINA; width = 1.30f; height = 0.84f;
            } else if (helmet.is(HaloFriends.WAKAMO.get())) {
                texture = WAKAMO; width = 0.92f; height = 0.92f;
            } else {
                return;
            }

            pose.pushPose();
            // Head animation pivot. -0.90 is above the top of a normal player head.
            getParentModel().head.translateAndRotate(pose);
            pose.translate(0.0D, -0.90D, 0.0D);
            // Rotate the thin sprite toward the camera. Position follows the head.
            pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            VertexConsumer vertex = buffers.getBuffer(RenderType.entityTranslucent(texture));
            float x = width * 0.5f;
            float y = height * 0.5f;
            quad(vertex, pose, -x, -y, x, y, false);
            quad(vertex, pose, -x, -y, x, y, true);
            pose.popPose();
        }

        private static void quad(VertexConsumer out, PoseStack pose,
                                 float left, float bottom, float right, float top, boolean reverse) {
            if (!reverse) {
                vertex(out, pose, left, bottom, 0, 1, 1);
                vertex(out, pose, right, bottom, 1, 1, 1);
                vertex(out, pose, right, top, 1, 0, 1);
                vertex(out, pose, left, top, 0, 0, 1);
            } else {
                vertex(out, pose, left, top, 0, 0, -1);
                vertex(out, pose, right, top, 1, 0, -1);
                vertex(out, pose, right, bottom, 1, 1, -1);
                vertex(out, pose, left, bottom, 0, 1, -1);
            }
        }

        private static void vertex(VertexConsumer out, PoseStack pose,
                                   float x, float y, float u, float v, float nz) {
            out.vertex(pose.last().pose(), x, y, 0)
                    .color(255, 255, 255, 255)
                    .uv(u, v)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(pose.last().normal(), 0, 0, nz)
                    .endVertex();
        }
    }
}
