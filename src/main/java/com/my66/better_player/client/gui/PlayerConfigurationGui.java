package com.my66.better_player.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.my66.better_player.BetterPlayer;
import com.my66.better_player.BetterPlayerConfig;
import com.my66.better_player.BetterPlayerMod;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@OnlyIn(Dist.CLIENT)
public class PlayerConfigurationGui extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/light_dirt_background.png");

    private EditBox firstNameBox;
    private EditBox lastNameBox;

    private Slider ageSlider;
    private ReadonlySlider pointsSlider;
    private Slider weightSlider;
    private Slider constitutionSlider;
    private Slider enduranceSlider;
    private Slider vitalcapacitySlider;
    private Button maleButton;
    private Button femaleButton;
    private Button eastButton;
    private Button westButton;

    private Button confirmButton;

    private final BetterPlayer betterPlayer;

    public PlayerConfigurationGui() {
        super(Component.literal("创建角色GUI"));
        betterPlayer = BetterPlayer.getBetterPlayer(Minecraft.getInstance().player);
    }

    @Override
    protected void init() {
        int height = this.height;
        int width = this.width;

        // 姓名输入框
        this.firstNameBox = new EditBox(this.font, 30, 160, 60, 10, Component.literal("名"));
        this.firstNameBox.setMaxLength(20);
        this.addRenderableWidget(this.firstNameBox);
        this.lastNameBox = new EditBox(this.font, 30, 174, 60, 10, Component.literal("姓"));
        this.lastNameBox.setMaxLength(20);
        this.addRenderableWidget(this.lastNameBox);

        // 性别按钮
        this.maleButton = Button.builder(Component.literal("♂"), btn -> setGender(true))
                .pos(10, 24)
                .size(40, 20)
                .build();
        this.femaleButton = Button.builder(Component.literal("♀"), btn -> setGender(false))
                .pos(60, 24)
                .size(40, 20)
                .build();
        //文化按钮
        this.eastButton = Button.builder(Component.literal("东方"), btn -> setCulture(true))
                .pos(10, 92)
                .size(40, 20)
                .build();
        this.westButton = Button.builder(Component.literal("西方"), btn -> setCulture(false))
                .pos(60, 92)
                .size(40, 20)
                .build();

        //保存和加载
        Button saveButton = Button.builder(Component.literal("保存"), btn -> onSubmit())
                .pos(10, height - 30)
                .size(40, 20)
                .build();
        Button loadButton = Button.builder(Component.literal("加载"), btn -> onLoad())
                .pos(60, height - 30)
                .size(40, 20)
                .build();
        this.confirmButton = Button.builder(Component.literal("确认"), btn -> onConfirm())
                .pos(width-50, height-30)
                .size(40, 20)
                .build();

        this.addRenderableWidget(maleButton);
        this.addRenderableWidget(femaleButton);
        this.addRenderableWidget(eastButton);
        this.addRenderableWidget(westButton);
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(loadButton);
        this.addRenderableWidget(confirmButton);
        updateGenderButtons();
        updateCultureButtons();

        // 预设点数条
        this.pointsSlider = new ReadonlySlider(width-130, 24, 120, 20, 0, BetterPlayerConfig.INSTANCE.getMaxPoints(), this.betterPlayer.getPoints());
        this.addRenderableWidget(this.pointsSlider);

        // 年龄滑动条
        this.ageSlider = new Slider(width-130, 65, 120, 20, 0, 80, this.betterPlayer.getAge());
        this.addRenderableWidget(this.ageSlider);

        // 体重滑动条
        this.weightSlider = new Slider(width-130, 106, 120, 20, 0, 250, this.betterPlayer.getWeight());
        this.addRenderableWidget(this.weightSlider);

        // 体质滑动条
        this.constitutionSlider = new Slider(width-130, 147, 120, 20, 10, 30, this.betterPlayer.get2530Constitution());
        this.addRenderableWidget(this.constitutionSlider);
        // 耐力滑动条
        this.enduranceSlider = new Slider(width-130, 168, 120, 20, 10, 30, this.betterPlayer.get2530Endurance());
        this.addRenderableWidget(this.enduranceSlider);
        // 肺活量滑动条
        this.vitalcapacitySlider = new Slider(width-130, 189, 120, 20, 10, 30, this.betterPlayer.get2530Vitalcapacity());
        this.addRenderableWidget(this.vitalcapacitySlider);
    }

    private void setGender(boolean male) {
        this.betterPlayer.setMale(male);
        updateGenderButtons();
    }

    private void setCulture(boolean east) {
        this.betterPlayer.setEast(east);
        updateCultureButtons();
    }

    private void updateGenderButtons() {
        this.maleButton.active = !this.betterPlayer.isMale();
        this.femaleButton.active = this.betterPlayer.isMale();
    }

    private void updateCultureButtons() {
        this.eastButton.active = !this.betterPlayer.isEast();
        this.westButton.active = this.betterPlayer.isEast();
    }

    public String getDataDir() {
        String version = SharedConstants.getCurrentVersion().getName(); // 获取当前游戏版本号
        String userHome = Minecraft.getInstance().gameDirectory.getAbsolutePath();
        String dd = userHome + "/versions/" + version + "/better_player/";
        File f = new File(dd);
        if (!f.exists()) {
            boolean r = f.mkdirs();
            if (r) System.out.println("create client data dir: " + f.getAbsolutePath());
            else System.out.println("error create client data dir: " + f.getAbsolutePath());
        }
        return dd;
    }

    private void updateBetterPlayerData() {
        betterPlayer.setFirstName(firstNameBox.getValue());
        betterPlayer.setLastName(lastNameBox.getValue());
        betterPlayer.setAge(ageSlider.get());
        betterPlayer.setWeight(weightSlider.get());
        betterPlayer.setPoints(pointsSlider.getValue());
        betterPlayer.setConstitution(constitutionSlider.get());
        betterPlayer.setEndurance(enduranceSlider.get());
        betterPlayer.setVitalcapacity(vitalcapacitySlider.get());
        betterPlayer.setWater(enduranceSlider.get());
        betterPlayer.setThirst(betterPlayer.getWater());
    }

    private void onSubmit() {
        updateBetterPlayerData();
        Minecraft.getInstance().setScreen(new SaveDialogGui(this, this.betterPlayer.getData()));
    }

    public void renderData(String data, int stage) {
        this.betterPlayer.setData(data);
        if (stage == 1) {
            this.ageSlider.set(betterPlayer.getAge());
            this.weightSlider.set(betterPlayer.getWeight());
            this.constitutionSlider.set(betterPlayer.get2530Constitution());
            this.enduranceSlider.set(betterPlayer.get2530Endurance());
            this.vitalcapacitySlider.set(betterPlayer.get2530Vitalcapacity());

            this.ageSlider.updateMessage();
            this.weightSlider.updateMessage();
            this.constitutionSlider.updateMessage();
            this.enduranceSlider.updateMessage();
            this.vitalcapacitySlider.updateMessage();

            updateGenderButtons();
            updateCultureButtons();
        }else if (stage == 2) {
            this.firstNameBox.setValue(betterPlayer.getFirstName());
            this.lastNameBox.setValue(betterPlayer.getLastName());
        }
    }

    private void onLoad() {
        updateBetterPlayerData();
        Minecraft.getInstance().setScreen(new LoadDialogGui(this, betterPlayer.getData()));
    }

    private void onConfirm() {
        updateBetterPlayerData();

        BetterPlayerMod.setPlayerDataLocal(Minecraft.getInstance().player, betterPlayer, true);
        BetterPlayerMod.setPlayerDataToServer(betterPlayer, true);

        this.onClose();
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
        gg.drawString(this.font, "性别", 10, 10, 0xFFFFFF);
        gg.drawString(this.font, "文化", 10, 78, 0xFFFFFF);
        gg.drawString(this.font, "角色姓名", 10, 146, 0xFFFFFF);
        gg.drawString(this.font, "名：", 10, 160, 0xFFFFFF);
        gg.drawString(this.font, "姓：", 10, 174, 0xFFFFFF);
        gg.drawString(this.font, "随机生成姓名：", 10, 188, 0xFFFFFF);

        gg.drawString(this.font, "预设点数", width-50, 10, 0xFFFFFF);
        gg.drawString(this.font, "年龄", width-30, 51, 0xFFFFFF);
        gg.drawString(this.font, "体重", width-30, 92, 0xFFFFFF);
        gg.drawString(this.font, "属性", width-30, 133, 0xFFFFFF);
        gg.drawString(this.font, "体质", width-150, 147, 0xFFFFFF);
        gg.drawString(this.font, "耐力", width-150, 168, 0xFFFFFF);
        gg.drawString(this.font, "肺活量", width-160, 189, 0xFFFFFF);
        super.render(gg, mouseX, mouseY, partialTicks);
    }

    // 年龄滑动条内部类
    private class Slider extends AbstractSliderButton {
        private final int min, max;

        public Slider(int x, int y, int width, int height, int min, int max, int value) {
            super(x, y, width, height, Component.literal(value + ""), (value - min) / (double)(max - min));
            this.min = min;
            this.max = max;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(Integer.toString(get())));
        }

        @Override
        protected void applyValue() {
            int v = BetterPlayerConfig.INSTANCE.getMaxPoints() - (
                    PlayerConfigurationGui.this.constitutionSlider.get() +
                            PlayerConfigurationGui.this.enduranceSlider.get() +
                            PlayerConfigurationGui.this.vitalcapacitySlider.get()
            );
            PlayerConfigurationGui.this.pointsSlider.setValue(v);
            PlayerConfigurationGui.this.pointsSlider.updateMessage();
            this.updateMessage();
            PlayerConfigurationGui.this.confirmButton.active = v >= 0;
        }

        public int get() {
            return (int)Math.round(this.value * (max - min) + min);
        }

        public void set(int v) {
            this.value = v;
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button) || this.firstNameBox.mouseClicked(x, y, button) || this.lastNameBox.mouseClicked(x, y, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.firstNameBox.keyPressed(keyCode, scanCode, modifiers) || this.lastNameBox.keyPressed(keyCode, scanCode, modifiers) || this.firstNameBox.canConsumeInput() || this.lastNameBox.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}