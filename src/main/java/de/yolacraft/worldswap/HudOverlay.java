package de.yolacraft.worldswap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class HudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(MatrixStack matrices, float tickDelta) {

        if (!RunState.timerRunning) return;
        if (RunState.displayText == null || RunState.displayText.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = client.textRenderer;

        int screenWidth = client.getWindow().getScaledWidth();
        int y = client.getWindow().getScaledHeight() - 70;

        String text1 = RunState.displayText;
        int text1Width = renderer.getWidth(text1);
        int x1 = screenWidth / 2 - text1Width - 2;

        drawOutlinedText(matrices, renderer, text1, x1, y, 0x54FCFC);

        String text2 = RunState.displayText2;
        int x2 = screenWidth / 2 + 2;

        drawOutlinedText(matrices, renderer, text2, x2, y, 0xFCFC54);
    }

    private void drawOutlinedText(MatrixStack matrices, TextRenderer renderer,
                                  String text, int x, int y, int color) {

        int outline = 0x000000;

        renderer.draw(matrices, text, x + 1, y, outline);
        renderer.draw(matrices, text, x - 1, y, outline);
        renderer.draw(matrices, text, x, y + 1, outline);
        renderer.draw(matrices, text, x, y - 1, outline);

        renderer.draw(matrices, text, x, y, color);
    }
}
