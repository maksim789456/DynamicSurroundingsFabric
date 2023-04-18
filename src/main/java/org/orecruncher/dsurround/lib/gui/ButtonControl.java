package org.orecruncher.dsurround.lib.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ButtonControl extends ButtonWidget {
    public ButtonControl(int i, int j, int k, int l, Text text, PressAction pressAction) {
        super(i, j, k, l, text, pressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }

    public ButtonControl(int i, int j, int k, int l, Text text, PressAction pressAction, NarrationSupplier narrationSupplier) {
        super(i, j, k, l, text, pressAction, narrationSupplier);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
