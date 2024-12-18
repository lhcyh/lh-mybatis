package io.github.lhcyh.lhswing;

import javax.swing.*;
import java.awt.*;

public class LhInput extends JTextField {
    private Integer padding=10;
    public LhInput(){
        //this.setSize(200,100);
        this.setText("");
        this.updateComponent();
    }

    private void updateComponent(){
        Font font=this.getFont();
        FontMetrics fontMetrics=sun.font.FontDesignMetrics.getMetrics(font);
        //int width=fontMetrics.stringWidth(this.getText());
        int height=fontMetrics.getHeight();
        this.setSize(this.getWidth(),height+padding);
    }

    public void setWidth(Integer width){
        this.setSize(width,this.getHeight());
        this.updateComponent();
    }

    public void setFontSize(float size){
        Font font=this.getFont().deriveFont(size);
        this.setFont(font);
        this.updateComponent();
    }
}
