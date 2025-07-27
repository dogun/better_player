package com.my66.better_player.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@OnlyIn(Dist.CLIENT)
public class SaveDialogGui extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/light_dirt_background.png");

    private final PlayerConfigurationGui parent;
    private final String data;
    private EditBox filenameBox;

    public SaveDialogGui(PlayerConfigurationGui parent, String data) {
        super(Component.literal("保存文件"));
        this.parent = parent;
        this.data = data;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.filenameBox = new EditBox(this.font, centerX - 60, centerY - 10, 120, 20, Component.literal("文件名"));
        this.filenameBox.setMaxLength(30);
        this.addRenderableWidget(this.filenameBox);

        this.addRenderableWidget(
                Button.builder(Component.literal("确定"), btn -> saveFile())
                        .pos(centerX - 60, centerY + 20)
                        .size(60, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("取消"), btn -> cancel())
                        .pos(centerX, centerY + 20)
                        .size(60, 20)
                        .build()
        );
    }

    private void saveFile() {
        String mcDir = parent.getDataDir();
        String filename = filenameBox.getValue().trim();
        File dataFile = new File(mcDir, filename + ".ss");
        try (FileWriter writer = new FileWriter(dataFile, StandardCharsets.UTF_8)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cancel();
    }

    private void cancel() {
        parent.renderData(this.data, 1);
        Minecraft.getInstance().setScreen(parent);
        parent.renderData(this.data, 2);
    }

    @Override
    public void render(@NotNull GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        int tileSize = 32;
        for (int x = 0; x < this.width; x += tileSize) {
            for (int y = 0; y < this.height; y += tileSize) {
                // 贴图区域不能超出屏幕
                int w = Math.min(tileSize, this.width - x);
                int h = Math.min(tileSize, this.height - y);
                gg.blit(BG_TEXTURE, x, y, 0, 0, w, h, tileSize, tileSize);
            }
        }

        gg.drawCenteredString(this.font, "保存角色", this.width / 2, this.height / 2 - 47, 0xFFFFFF);
        gg.drawCenteredString(this.font, "存档名：", this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        super.render(gg, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button) || this.filenameBox.mouseClicked(x, y, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.filenameBox.keyPressed(keyCode, scanCode, modifiers) || this.filenameBox.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}