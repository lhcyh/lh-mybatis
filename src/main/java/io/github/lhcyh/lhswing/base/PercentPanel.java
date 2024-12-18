package io.github.lhcyh.lhswing.base;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @ClassName：PercentPanel
 * @Author：lh
 * @Date：2024/12/4 15:47
 */
public class PercentPanel extends JPanel {
    private ComponentAdapter pWidthListen;
    private ComponentAdapter pHeightListen;

    public void setWidthPercent(Float widthPercent) {
        Container parent=this.getParent();
        this.removePWidthListen();
        this.pWidthListen=new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                PercentPanel.super.setSize((int) (parent.getWidth()*widthPercent),getHeight());
            }
        };
        parent.addComponentListener(this.pWidthListen);
        super.setSize((int) (parent.getWidth()*widthPercent),this.getHeight());
    }

    public void setHeightPercent(Float heightPercent){
        Container parent=this.getParent();
        this.removePHeightListen();
        this.pHeightListen=new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                PercentPanel.super.setSize(getWidth(),(int) (parent.getHeight()*heightPercent));
            }
        };
        parent.addComponentListener(this.pHeightListen);
        super.setSize(getWidth(),(int) (parent.getHeight()*heightPercent));
    }

    protected void removePWidthListen(){
        if(this.pWidthListen!=null){
            Container parent=this.getParent();
            parent.removeComponentListener(this.pWidthListen);
            this.pWidthListen=null;
        }
    }

    protected void removePHeightListen(){
        if(this.pHeightListen!=null){
            Container parent=this.getParent();
            parent.removeComponentListener(this.pHeightListen);
            this.pHeightListen=null;
        }
    }

    public void setBorder(Integer thickness,Color color){
        // 创建并设置边框
        Border border = BorderFactory.createLineBorder(color, thickness); // 2px边框
        super.setBorder(border);
    }

    public void setWidth(int width){
        this.setSize(width,this.getHeight());
        this.removePWidthListen();
    }

    public void setHeight(int height){
        this.setSize(this.getWidth(),height);
        this.removePHeightListen();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.removePWidthListen();
        this.removePHeightListen();
    }

}
