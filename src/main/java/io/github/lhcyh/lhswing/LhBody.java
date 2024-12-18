package io.github.lhcyh.lhswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @ClassName：Body
 * @Author：lh
 * @Date：2024/12/3 11:17
 */
public class LhBody extends JFrame {
    private LhDiv lhDiv;

    public LhBody(String title){
        super(title);
        // 设置窗口的属性
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.listenSize();

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // 设置窗口尺寸
        int windowWidth = (int) (screenWidth*0.7);
        int windowHeight = (int) (screenHeight*0.7);
        this.setSize(windowWidth,windowHeight);

        // 计算窗口居中位置
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;

        // 设置窗口位置
        setLocation(x, y);

        this.setVisible(true);
    }

    private void listenSize(){
        LhBody that=this;
        this.addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                int topHeight=60;
                LhDiv div=that.getDiv();
                div.setSize(that.getWidth(),that.getHeight()-topHeight);
            }
        });
    }

    public LhDiv getDiv(){
        if(this.lhDiv==null){
            this.lhDiv=new LhDiv();
            this.add(lhDiv);
        }
        return this.lhDiv;
    }
}
