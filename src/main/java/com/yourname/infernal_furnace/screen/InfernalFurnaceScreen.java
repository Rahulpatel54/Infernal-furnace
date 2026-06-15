package com.yourname.infernal_furnace.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yourname.infernal_furnace.InfernalFurnaceMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class InfernalFurnaceScreen extends HandledScreen<InfernalFurnaceScreenHandler> {

    // Reuse vanilla furnace texture
    private static final Identifier TEXTURE =
            Identifier.of("minecraft", "textures/gui/container/furnace.png");

    public InfernalFurnaceScreen(InfernalFurnaceScreenHandler handler,
                                  PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // Draw the full vanilla furnace background
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        // Draw cook progress arrow
        int cookProgress = this.handler.getCookProgress();
        context.drawTexture(TEXTURE, x + 79, y + 34, 176, 14, cookProgress + 1, 16);

        // Draw fire animation only if burning (always true when lit)
        if (this.handler.isBurning()) {
            context.drawTexture(TEXTURE, x + 56, y + 36, 176, 30, 14, 14);
        }

        // Paint over the fuel slot with the background color to hide it
        // The fuel slot is at x+56, y+36 in GUI coords (relative to background)
        context.drawTexture(TEXTURE, x + 55, y + 35, 0, 0, 18, 18);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}