package de.yolacraft.worldswap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class AutoClickCreateWorldScreen extends CreateWorldScreen {

    private boolean clicked = false;
    private int restoreDelay = 0;

    public AutoClickCreateWorldScreen(Screen parent) {
        super(parent);
    }

    @Override
    public void tick() {
        super.tick();

        if (!clicked && !this.children().isEmpty()) {
            ButtonWidget createButton = (ButtonWidget) this.children().get(13);
            createButton.onPress();
            clicked = true;

            restoreDelay = 3;
        }
    }
}
