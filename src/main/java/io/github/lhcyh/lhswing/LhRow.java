package io.github.lhcyh.lhswing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName：LhRow
 * @Author：lh
 * @Date：2024/12/4 17:04
 */
public class LhRow extends LhDiv{
    private List<LhDiv> cellList=new ArrayList<>();
    private Integer padding;
    private JustifyContent justifyContent;
    private AlignItems alignItems;
    private Border border;

    public LhRow(Integer padding){
        super();
        this.padding=padding;
        //this.setBorder(1,Color.GRAY);
    }

    public Integer addComponent(JComponent jComponent){
        LhDiv cell=new LhDiv();
        jComponent.addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                cell.restoreAutoSize();
            }
        });

        cell.setPadding(padding);
        //cell.setBorder(1, Color.GRAY);
        if(border!=null){
            cell.setBorder(border);
        }
        cell.setJustifyContent(justifyContent);
        cell.setAlignItems(alignItems);
        cell.add(jComponent);
        this.add(cell);
        cell.addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                updateRow();
            }
        });
        cellList.add(cell);
        this.updateRow();
        return cellList.size()-1;
    }

    public Integer getCellWidth(Integer index){
        if(index>=this.cellList.size()){
            return null;
        }
        return this.cellList.get(index).getWidth();
    }

    public void setCellWidth(Integer index,Integer width){
        if(index<this.cellList.size()){
            this.cellList.get(index).setWidth(width);
        }
    }

//    @Override
//    public void setPadding(Integer padding){
//        for(LhDiv cell:cellList){
//            cell.setPadding(padding);
//        }
//    }

    private Integer getMaxHeight(){
        Integer max=0;
        for(LhDiv cell:cellList){
            if(cell.getHeight()>max){
                max=cell.getHeight();
            }
        }
        return max;
    }

    private void updateRow(){
        Integer max=getMaxHeight();
        for(LhDiv cell:cellList){
            cell.setHeight(max);
        }
    }

    @Override
    public void setBorder(Border border) {
        //super.setBorder(thickness, color);
//        for(LhDiv cell:cellList){
//            cell.setBorder(thickness,color);
//        }
        this.border=border;
    }

    @Override
    public void setAlignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
    }

    @Override
    public void setJustifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
    }
}
