package io.github.lhcyh.lhswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @ClassName：LhButton
 * @Author：lh
 * @Date：2024/12/3 12:47
 */
public class LhButton extends JButton {
    private int padding=10;

    public LhButton(String text){
        super();
        this.setText(text);
    }

    private void updateComponent(){
        Font font=this.getFont();
        FontMetrics fontMetrics=sun.font.FontDesignMetrics.getMetrics(font);
        int width=fontMetrics.stringWidth(this.getText());
        int height=fontMetrics.getHeight();
        this.setSize(width+34+padding,height+padding);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        this.updateComponent();
    }

    public void setFontSize(float size){
        Font font=this.getFont();
        Font newFont=font.deriveFont(size);
        this.setFont(newFont);
        this.updateComponent();
    }

    public void setPadding(int padding) {
        this.padding = padding;
        this.updateComponent();
    }

    public void removeAllActionListener() {
        ActionListener[] actionListeners=this.getActionListeners();
        for(ActionListener actionListener:actionListeners){
            this.removeActionListener(actionListener);
        }
    }
}
