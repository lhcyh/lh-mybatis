package io.github.lhcyh.lhswing;

import javax.swing.*;
import java.awt.*;

/**
 * @ClassName：LhRadio
 * @Author：lh
 * @Date：2024/12/6 16:38
 */
public class LhRadio extends JRadioButton {
    private Integer padding=5;

    public LhRadio(String text){
        super(text);
        this.updateComponent();
    }

    public void setFontSize(float size){
        Font font=this.getFont().deriveFont(size);
        this.setFont(font);
        this.updateComponent();
    }

    private void updateComponent(){
        Font font=this.getFont();
        FontMetrics fontMetrics=sun.font.FontDesignMetrics.getMetrics(font);
        int width=fontMetrics.stringWidth(this.getText());
        int height=fontMetrics.getHeight();
        this.setSize(width+30+padding,height+padding);
    }

    public void setPadding(Integer padding) {
        this.padding = padding;
        this.updateComponent();
    }
}
