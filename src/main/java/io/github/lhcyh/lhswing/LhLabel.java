package io.github.lhcyh.lhswing;

import javax.swing.*;
import java.awt.*;

/**
 * @ClassName：LhLabel
 * @Author：lh
 * @Date：2024/12/3 16:29
 */
public class LhLabel extends JLabel {
    private int padding=0;
    public LhLabel(String text){
        super(text,JLabel.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        //this.updateComponent();
    }

    private void updateComponent(){
        Font font=this.getFont();
        if(font==null){
            font=new Font("宋体",Font.PLAIN,25);
            this.setFont(font);
        }
        FontMetrics fontMetrics=sun.font.FontDesignMetrics.getMetrics(font);
        int width=fontMetrics.stringWidth(this.getText());
        int height=fontMetrics.getHeight();
        this.setSize(width+this.padding*2,height+this.padding*2);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        this.updateComponent();
    }

    public void setPadding(int padding) {
        this.padding = padding;
        this.updateComponent();
    }

    public void setFontSize(float size){
        Font font=this.getFont().deriveFont(size);
        this.setFont(font);
        updateComponent();
    }
}
