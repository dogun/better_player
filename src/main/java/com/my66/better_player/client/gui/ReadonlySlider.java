package com.my66.better_player.client.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReadonlySlider extends AbstractSliderButton {
    private final int min, max;
    private int v;

    public ReadonlySlider(int x, int y, int width, int height, int min, int max, int v) {
        super(x, y, width, height, Component.literal(v + ""), (double)(v - min) / (double)(max - min));
        this.min = min;
        this.max = max;
        this.v = v;
        this.updateMessage();
    }

    public void setValue(int v) {
        this.v = v;
        this.value = (double)(v - min) / (double)(max - min);
    }

    public int getValue() {
        return this.v;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        // 不做任何事，禁止拖动
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal(Integer.toString(this.v)));
    }

    @Override
    protected void applyValue() {

    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false; // 不处理拖动
    }
}