package com.my66.better_player.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LoadDialogGui extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/light_dirt_background.png");

    private final PlayerConfigurationGui parent;
    private String data;

    private final List<String> options;
    private int selected = 0;
    private int scroll = 0;
    private final int listWidth = 180;
    private final int listHeight = 120;
    private final int itemHeight = 22;

    public LoadDialogGui(PlayerConfigurationGui parent, String data) {
        super(Component.literal("载入角色GUI"));
        this.parent = parent;
        this.data = data;
        File dir = new File(parent.getDataDir());
        List<String> list = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] fs = dir.listFiles();
            if (fs != null) {
                for(File f : fs) {
                    list.add(f.getName());
                }
            }
        }
        this.options = list;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        Button okButton = Button.builder(Component.literal("确定"), btn -> this.ok())
                .pos(centerX - 65, height - 34).size(60, 20).build();

        Button cancelButton = Button.builder(Component.literal("取消"), btn -> this.cancel())
                .pos(centerX + 5, height - 34).size(60, 20).build();

        addRenderableWidget(okButton);
        addRenderableWidget(cancelButton);
    }

    private void ok() {
        String selectedOption = getSelectedOption();
        if (selectedOption != null) {
            try {
                this.data = new String(Files.readAllBytes(Paths.get(parent.getDataDir() + "/" + selectedOption)), StandardCharsets.UTF_8);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        cancel();
    }

    private void cancel() {
        parent.renderData(this.data, 1);
        Minecraft.getInstance().setScreen(parent);
        parent.renderData(this.data, 2);
    }
    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gg);
        int tileSize = 32;
        for (int x = 0; x < this.width; x += tileSize) {
            for (int y = 0; y < this.height; y += tileSize) {
                // 贴图区域不能超出屏幕
                int w = Math.min(tileSize, this.width - x);
                int h = Math.min(tileSize, this.height - y);
                gg.blit(BG_TEXTURE, x, y, 0, 0, w, h, tileSize, tileSize);
            }
        }

        gg.drawCenteredString(this.font, "载入角色", this.width / 2, 24, 0xFFFFFF);

        // 列表背景
        gg.fill(0, 0, this.width, this.height, 0x80000000);

        for (int x = 0; x < this.width; x += tileSize) {
            for (int y = 0; y < 48; y += tileSize) {
                // 贴图区域不能超出屏幕
                int w = Math.min(tileSize, this.width - x);
                int h = Math.min(tileSize, this.height - y);
                if (48 - y < 32) {
                    h = 16;
                }
                gg.blit(BG_TEXTURE, x, y, 0, 0, w, h, tileSize, tileSize);
            }
        }

        for (int x = 0; x < this.width; x += tileSize) {
            for (int y = this.height - 48; y < this.height; y += tileSize) {
                // 贴图区域不能超出屏幕
                int w = Math.min(tileSize, this.width - x);
                int h = Math.min(tileSize, this.height - y);
                gg.blit(BG_TEXTURE, x, y, 0, 0, w, h, tileSize, tileSize);
            }
        }

        int centerX = this.width / 2;
        int x = centerX - listWidth / 2;
        int y = 60;

        int visibleCount = listHeight / itemHeight;
        int maxScroll = Math.max(0, options.size() - visibleCount);

        // 列表项目
        for (int i = 0; i < visibleCount && (i + scroll) < options.size(); i++) {
            int itemY = y + i * itemHeight;
            boolean isSelected = (i + scroll) == selected;
            int color = isSelected ? 0xFF4488FF : 0xFF444444;
            gg.fill(x + 2, itemY + 2, x + listWidth - 2, itemY + itemHeight - 2, color);

            // 单选圆点
            int dotX = x + 12, dotY = itemY + itemHeight/2 - 3;
            gg.fill(dotX-1, dotY-1, dotX+7, dotY+7, 0xFFBBBBBB);
            if (isSelected)
                gg.fill( dotX+1, dotY+1, dotX+5, dotY+5, 0xFF2255DD);

            // 文字
            gg.drawString(this.font, options.get(i + scroll), x + 26, itemY + 6, 0xFFFFFF);
        }

        // 滚动条
        if (options.size() > visibleCount) {
            int barH = Math.max(16, (int)((listHeight - 4) * (visibleCount/(float)options.size())));
            int barY = y + 2 + (listHeight - 4 - barH) * scroll / maxScroll;
            gg.fill( x + listWidth - 8, barY, x + listWidth - 4, barY + barH, 0xFF888888);
        }

        super.render(gg, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int x = centerX - listWidth / 2;
        int y = 60;

        if (mouseX >= x && mouseX <= x + listWidth && mouseY >= y && mouseY <= y + listHeight) {
            int idx = (int)((mouseY - y) / itemHeight) + scroll;
            if (idx >= 0 && idx < options.size()) {
                selected = idx;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int centerX = this.width / 2;
        int x = centerX - listWidth / 2;
        int y = 60;
        int visibleCount = listHeight / itemHeight;
        int maxScroll = Math.max(0, options.size() - visibleCount);

        if (mouseX >= x && mouseX <= x + listWidth && mouseY >= y && mouseY <= y + listHeight && maxScroll > 0) {
            scroll = Math.max(0, Math.min(maxScroll, scroll - (int)Math.signum(delta)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public String getSelectedOption() {
        return selected >= 0 && selected < options.size() ? options.get(selected) : null;
    }
}