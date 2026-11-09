package jp.halofriends.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Static 3D extrusion mesh. Quads are read from compact binary resources.
 * Triangle surfaces are stored as degenerate quads for Minecraft's QUADS renderer.
 * The data is entirely generated at development time; no runtime Python needed.
 */
public final class HaloMesh3D {
    private final Vertex[][] faces;
    private final Vertex[][] walls;

    private record Vertex(float x, float y, float z, float u, float v,
                          float nx, float ny, float nz, int r, int g, int b, int a) {
    }

    private HaloMesh3D(Vertex[][] faces, Vertex[][] walls) {
        this.faces = faces;
        this.walls = walls;
    }

    static HaloMesh3D load(String name) {
        String path = "/assets/halo_friends/meshes/" + name + ".hl3d";
        InputStream resource = HaloMesh3D.class.getResourceAsStream(path);
        if (resource == null) throw new IllegalStateException("Missing halo mesh: " + path);
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(resource))) {
            byte[] magic = new byte[4];
            input.readFully(magic);
            if (magic[0] != 'H' || magic[1] != 'L' || magic[2] != '3' || magic[3] != 'D') {
                throw new IOException("Invalid 3D halo mesh header: " + path);
            }
            int faceCount = input.readInt();
            int wallCount = input.readInt();
            if (faceCount < 0 || faceCount > 20000 || wallCount < 0 || wallCount > 20000) {
                throw new IOException("Invalid 3D halo mesh size: " + path);
            }
            Vertex[][] faces = readQuads(input, faceCount);
            Vertex[][] walls = readQuads(input, wallCount);
            return new HaloMesh3D(faces, walls);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load 3D halo mesh: " + path, e);
        }
    }

    private static Vertex[][] readQuads(DataInputStream in, int count) throws IOException {
        Vertex[][] quads = new Vertex[count][4];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 4; j++) {
                quads[i][j] = new Vertex(
                        in.readFloat(), in.readFloat(), in.readFloat(),
                        in.readFloat(), in.readFloat(),
                        in.readFloat(), in.readFloat(), in.readFloat(),
                        in.readUnsignedByte(), in.readUnsignedByte(),
                        in.readUnsignedByte(), in.readUnsignedByte());
            }
        }
        return quads;
    }

    void renderFrontBack(PoseStack pose, VertexConsumer consumer) {
        renderQuads(pose, consumer, faces, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    void renderFrontBack(PoseStack pose, VertexConsumer consumer,
                         float rMul, float gMul, float bMul, float aMul) {
        renderQuads(pose, consumer, faces, rMul, gMul, bMul, aMul);
    }

    void renderWalls(PoseStack pose, VertexConsumer consumer) {
        renderQuads(pose, consumer, walls, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    void renderWalls(PoseStack pose, VertexConsumer consumer,
                     float rMul, float gMul, float bMul, float aMul) {
        renderQuads(pose, consumer, walls, rMul, gMul, bMul, aMul);
    }

    private static void renderQuads(PoseStack pose, VertexConsumer consumer, Vertex[][] quads,
                                    float rMul, float gMul, float bMul, float aMul) {
        for (Vertex[] quad : quads) {
            for (Vertex v : quad) {
                int r = clamp255(v.r(), rMul);
                int g = clamp255(v.g(), gMul);
                int b = clamp255(v.b(), bMul);
                int a = clamp255(v.a(), aMul);
                consumer.vertex(pose.last().pose(), v.x(), v.y(), v.z())
                        .color(r, g, b, a)
                        .uv(v.u(), v.v())
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BRIGHT)
                        .normal(pose.last().normal(), v.nx(), v.ny(), v.nz())
                        .endVertex();
            }
        }
    }

    private static int clamp255(int value, float mul) {
        return Math.max(0, Math.min(255, Math.round(value * mul)));
    }
}
