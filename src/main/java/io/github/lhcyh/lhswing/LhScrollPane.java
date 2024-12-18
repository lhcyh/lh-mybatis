package io.github.lhcyh.lhswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @ClassName：LhScrollPane
 * @Author：lh
 * @Date：2024/12/3 17:25
 */
public class LhScrollPane extends JScrollPane {
    private LhDiv lhDiv;
    private ComponentAdapter pWidthListen;
    private ComponentAdapter pHeightListen;

    public LhScrollPane(){
        super();
        this.lhDiv=new LhDiv();
        JPanel jPanel=new JPanel();
        jPanel.setLayout(null);
        jPanel.add(this.lhDiv);
        this.setViewportView(jPanel);
        this.lhDiv.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                jPanel.setSize(getWidth(),getHeight());
                Dimension dimension=new Dimension(lhDiv.getWidth(),lhDiv.getHeight());
                jPanel.setPreferredSize(dimension);
            }
        });

    }

    public LhDiv getDiv(){
        return this.lhDiv;
    }

    public void setWidthPercent(Float widthPercent) {
        if(widthPercent>0&&widthPercent<=1){
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
                    LhScrollPane.super.setSize((int) (parent.getWidth()*widthPercent),getHeight());
                }
            };
            parent.addComponentListener(this.pWidthListen);
            super.setSize((int) (parent.getWidth()*widthPercent),this.getHeight());
        }
    }

    public void setHeightPercent(Float heightPercent){
        if(heightPercent>0&&heightPercent<=1){
            Container parent=this.getParent();
            this.removePHeightListen();
            this.pHeightListen=new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    LhScrollPane.super.setSize(getWidth(),(int) (parent.getHeight()*heightPercent));
                }
            };
            parent.addComponentListener(this.pHeightListen);
            super.setSize(getWidth(),(int) (parent.getHeight()*heightPercent));
        }
    }

    private void removePWidthListen(){
        if(this.pWidthListen!=null){
            Container parent=this.getParent();
            parent.removeComponentListener(this.pWidthListen);
            this.pWidthListen=null;
        }
    }

    private void removePHeightListen(){
        if(this.pHeightListen!=null){
            Container parent=this.getParent();
            parent.removeComponentListener(this.pHeightListen);
            this.pHeightListen=null;
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.removePWidthListen();
        this.removePHeightListen();
    }

    public void setWidth(int width){
        this.setSize(width,this.getHeight());
        this.removePWidthListen();
    }

    public void setHeight(int height){
        this.setSize(this.getWidth(),height);
        this.removePHeightListen();
    }
}
