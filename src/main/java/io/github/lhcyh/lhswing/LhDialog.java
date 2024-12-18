package io.github.lhcyh.lhswing;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @ClassName：DivDialog
 * @Author：lh
 * @Date：2024/12/3 10:17
 */
public class LhDialog extends JDialog {
    private LhDiv lhDiv;
    public LhDialog(JFrame jFrame,String title){
        super(jFrame,title);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setModal(true);
        int dWidth=(int) (jFrame.getWidth()*0.6);
        int dHeight=(int) (jFrame.getHeight()*0.8);
        int dx=jFrame.getX()+(jFrame.getWidth()-dWidth)/2;
        int dy=jFrame.getY()+(jFrame.getHeight()-dHeight)/2;
        this.setSize(dWidth,dHeight);
        this.setLocation(dx,dy);
    }

    public LhDiv getDiv(){
        if(this.lhDiv==null){
            this.lhDiv=new LhDiv();
            //this.lhDiv.setBorder(2,Color.black);
            this.lhDiv.setJustifyContent(JustifyContent.CENTER);
            this.lhDiv.setAlignItems(AlignItems.CENTER);
            this.add(this.lhDiv);
            this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    lhDiv.setSize(getWidth(),getHeight());
                }
            });
            lhDiv.setSize(getWidth(),getHeight());
        }
        return this.lhDiv;
    }
}
