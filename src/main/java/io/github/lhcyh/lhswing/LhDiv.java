package io.github.lhcyh.lhswing;

import io.github.lhcyh.lhswing.base.PercentPanel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName：LhDiv
 * @Author：lh
 * @Date：2024/12/3 11:50
 */
public class LhDiv extends PercentPanel {
    private List<Component> componentList=new ArrayList<>();
    private Integer padding=0;
    private Boolean autoWidth=true;
    private Boolean autoHeight=true;

    private FlexDirection flexDirection=FlexDirection.ROW;
    private JustifyContent justifyContent=JustifyContent.FLEX_START;
    private AlignItems alignItems=AlignItems.FLEX_START;

    public LhDiv(){
        this.setLayout(null);
        // 创建并设置边框
//        Border border = BorderFactory.createLineBorder(Color.BLACK, 2); // 2px边框
//        this.setBorder(border);
        this.addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                updateComponent();
            }
        });
    }

    private Integer getSumComponentWidth(){
        Integer sum=0;
        for(Component component:componentList){
            sum+=component.getWidth();
        }
        return sum;
    }

    private Integer getSumComponentHeight(){
        Integer sum=0;
        for (Component component:componentList){
            sum+=component.getHeight();
        }
        return sum;
    }

    private Integer getMaxWidth(){
        Integer maxWidth=0;
        for (Component component:componentList){
            Integer width=component.getWidth();
            if(width>maxWidth){
                maxWidth=width;
            }
        }
        return maxWidth;
    }

    private Integer getMaxHeight(){
        Integer maxHeight=0;
        for(Component component:componentList){
            Integer height=component.getHeight();
            if(height>maxHeight){
                maxHeight=height;
            }
        }
        return maxHeight;
    }


    private void setAutoSize(){
        if(this.autoWidth){
            Integer width=0;
            switch (this.flexDirection){
                case ROW:
                    width=this.getSumComponentWidth();
                    break;
                case COLUMN:
                    width=this.getMaxWidth();
                    break;
            }
            super.setSize(width+this.padding*2,this.getHeight());
        }
        if(this.autoHeight){
            Integer height=0;
            switch (this.flexDirection){
                case ROW:
                    height=this.getMaxHeight();
                    break;
                case COLUMN:
                    height=this.getSumComponentHeight();
                    break;
            }
            super.setSize(this.getWidth(),height+this.padding*2);
        }
    }

    public void updateComponent(){
//        try {
            this.setAutoSize();
//        }catch (Exception e){
//            System.out.println("setAutoSizeException");
//        }

        switch (flexDirection){
            case ROW:
                Integer sum=getSumComponentWidth();
                switch (justifyContent){
                    case FLEX_START:
                        for(int i=0;i<this.componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(padding,padding);
                            }else {
                                Component last=this.componentList.get(i-1);
                                this.componentList.get(i).setLocation(last.getLocation().x+last.getWidth(),padding);
                            }
                        }
                        break;
                    case CENTER:
                        Integer space=this.getWidth()-sum;
                        for(int i=0;i<this.componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(space/2,padding);
                            }else {
                                Component last=this.componentList.get(i-1);
                                this.componentList.get(i).setLocation(last.getLocation().x+last.getWidth(),padding);
                            }
                        }
                        break;
                    case FLEX_END:
                        for(int i=this.componentList.size()-1;i>=0;i--){
                            if(i==this.componentList.size()-1){
                                int cx=this.getWidth()-(padding+this.componentList.get(i).getWidth());
                                this.componentList.get(i).setLocation(cx,padding);
                            }else {
                                int cx=this.componentList.get(i+1).getLocation().x-this.componentList.get(i).getWidth();
                                this.componentList.get(i).setLocation(cx,padding);
                            }
                        }
                        break;
                    case SPACE_AROUND:
                        int aSpace=(this.getWidth()-sum-padding*2)/(this.componentList.size()+1);
                        for(int i=0;i<this.componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(padding+aSpace,padding);
                            }else {
                                Component component=this.componentList.get(i-1);
                                int cx=component.getLocation().x+component.getWidth()+aSpace;
                                this.componentList.get(i).setLocation(cx,padding);
                            }
                        }
                        break;
                    case SPACE_BETWEEN:
                        int bSpace=(this.getWidth()-sum-padding*2)/(this.componentList.size()-1);
                        for(int i=0;i<this.componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(padding,padding);
                            }else {
                                Component component=this.componentList.get(i-1);
                                int cx=component.getLocation().x+component.getWidth()+bSpace;
                                this.componentList.get(i).setLocation(cx,padding);
                            }
                        }
                        break;
                }

//                Integer maxHeight=this.getMaxHeight();
                switch (alignItems){
                    case FLEX_START:
                        break;
                    case CENTER:
                        for(Component component:componentList){
                            Integer cy=this.getHeight()-component.getHeight();
                            cy=cy/2;
                            component.setLocation(component.getX(),cy);
                        }
                        break;
                    case FLEX_END:
                        for(Component component:componentList){
                            Integer cy=this.getHeight()-component.getHeight()-padding;
                            component.setLocation(component.getX(),cy);
                        }
                        break;
                }
                break;
            case COLUMN:
                Integer hSum=getSumComponentHeight();
                switch (justifyContent){
                    case FLEX_START:
                        for(int i=0;i<componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(padding,padding);
                            }else {
                                Component last=this.componentList.get(i-1);
                                int cy=last.getY()+last.getHeight();
                                this.componentList.get(i).setLocation(padding,cy);
                            }
                        }
                        break;
                    case CENTER:
                        int cSpace=this.getHeight()-hSum;
                        for(int i=0;i<componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(padding,cSpace/2);
                            }else {
                                Component last=this.componentList.get(i-1);
                                int cy=last.getY()+last.getHeight();
                                this.componentList.get(i).setLocation(padding,cy);
                            }
                        }
                        break;
                    case FLEX_END:
                        for(int i=this.componentList.size()-1;i<=0;i--){
                            if(i==this.componentList.size()-1){
                                Component component=this.componentList.get(i);
                                Integer cy=this.getHeight()-component.getHeight()-padding;
                                component.setLocation(padding,cy);
                            }else {
                                Component next=this.componentList.get(i+1);
                                Component component=this.componentList.get(i);
                                Integer cy=next.getY()-component.getHeight();
                                component.setLocation(padding,cy);
                            }
                        }
                        break;
                    case SPACE_AROUND:
                        Integer aSpace=(this.getHeight()-padding*2-hSum)/(this.componentList.size()+1);
                        for(int i=0;i<this.componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(padding,padding+aSpace);
                            }else {
                                Component last=this.componentList.get(i-1);
                                Integer cy=last.getY()+last.getHeight()+aSpace;
                                this.componentList.get(i).setLocation(padding,cy);
                            }
                        }
                        break;
                    case SPACE_BETWEEN:
                        Integer bSpace=(this.getHeight()-padding*2-hSum);
                        if(this.componentList.size()==1){
                            bSpace=bSpace/2;
                        }else {
                            bSpace=bSpace/(this.componentList.size()-1);
                        }
                        for(int i=0;i<this.componentList.size();i++){
                            if(i==0){
                                this.componentList.get(i).setLocation(padding,padding);
                            }else {
                                Component last=this.componentList.get(i-1);
                                Integer cy=last.getY()+last.getHeight()+bSpace;
                                this.componentList.get(i).setLocation(padding,cy);
                            }
                        }
                        break;
                }

                switch (alignItems){
                    case FLEX_START:
                        break;
                    case FLEX_END:
                        for (Component component:componentList){
                            Integer cx=this.getWidth()-padding-component.getWidth();
                            component.setLocation(cx,component.getY());
                        }
                        break;
                    case CENTER:
                        for(Component component:componentList){
                            Integer cx=this.getWidth()-component.getWidth();
                            cx=cx/2;
                            component.setLocation(cx,component.getY());
                        }
                        break;
                }

        }
        this.repaint();
    }

    @Override
    public Component add(Component comp) {
        super.add(comp);
        this.componentList.add(comp);
        this.updateComponent();
        comp.addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                //setAutoSize();
                updateComponent();
            }
        });

        return comp;
    }

    @Override
    public void setWidthPercent(Float widthPercent) {
        super.setWidthPercent(widthPercent);
        this.autoWidth=false;
    }

    @Override
    public void setHeightPercent(Float heightPercent){
        super.setHeightPercent(heightPercent);
        this.autoHeight=false;
    }

    @Override
    public void setWidth(int width){
        this.autoWidth=false;
        super.setWidth(width);
    }

    @Override
    public void setHeight(int height){
        this.autoHeight=false;
        super.setHeight(height);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.autoWidth=false;
        this.autoHeight=false;
    }

    public void setPadding(Integer padding) {
        this.padding = padding;
        updateComponent();
    }

    public void setFlexDirection(FlexDirection flexDirection) {
        this.flexDirection = flexDirection;
    }

    public void setJustifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
    }

    public void setAlignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
    }

    @Override
    public void remove(Component comp) {
        super.remove(comp);
        this.componentList.remove(comp);
        updateComponent();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        this.componentList.clear();
        updateComponent();
    }

    public void restoreAutoSize(){
        removePHeightListen();
        removePWidthListen();
        this.autoWidth=true;
        this.autoHeight=true;
        this.updateComponent();
    }
}
