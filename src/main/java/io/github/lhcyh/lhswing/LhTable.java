package io.github.lhcyh.lhswing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName：LhTable
 * @Author：lh
 * @Date：2024/12/4 16:18
 */
public class LhTable extends LhDiv {
    private List<LhRow> rowList=new ArrayList<>();
    private Integer padding=2;
    private JustifyContent justifyContent=JustifyContent.FLEX_START;
    private AlignItems alignItems=AlignItems.CENTER;
    private Border border;

    private void init(){
        this.setFlexDirection(FlexDirection.COLUMN);
        //this.setBorder(1,Color.GRAY);
        super.setPadding(5);
    }

    public LhTable(){
        super();
        this.init();
    }

    public LhTable(Integer padding){
        super();
        this.init();
        this.padding=padding;
    }

    public LhRow addRow(){
        LhRow lhRow=new LhRow(this.padding);
        lhRow.setAlignItems(alignItems);
        lhRow.setJustifyContent(justifyContent);
        if(this.border!=null){
            lhRow.setBorder(border);
        }
        lhRow.addComponentListener(new ComponentAdapter() {
            /**
             * Invoked when the component's size changes.
             *
             * @param e
             */
            @Override
            public void componentResized(ComponentEvent e) {
                updateTable();
            }
        });
        this.add(lhRow);
        rowList.add(lhRow);
        //this.updateTable();
        return lhRow;
    }

    public void removeRow(LhRow row){
        rowList.remove(row);
        this.remove(row);
        updateTable();
    }

    private Integer getColMaxWidth(Integer index){
        Integer maxWidth=null;
        for(LhRow row:rowList){
            if(maxWidth==null){
                maxWidth=row.getCellWidth(index);
            }else {
                Integer width=row.getCellWidth(index);
                if(width!=null&&width>maxWidth){
                    maxWidth=width;
                }
            }
        }
        return maxWidth;
    }

    private void unifiedColWidth(){
        try {
            Integer index=0;
            while (true){
                Integer maxWidth=this.getColMaxWidth(index);
                if (maxWidth==null){
                    break;
                }else {
                    for(LhRow row:rowList){
                        row.setCellWidth(index,maxWidth);
                    }
                }
                index++;
            }
        }catch (Exception e){
            System.out.println("e");
        }

    }

    private void updateTable(){
        this.unifiedColWidth();
//        for(LhRow lhRow:rowList){
//            lhRow.setPadding(this.padding);
//        }
    }

    @Override
    public void setBorder(Integer thickness, Color color) {
        super.setBorder(thickness, color);
        this.border= BorderFactory.createLineBorder(color, thickness);
//        for(LhRow row:rowList){
//            row.setBorder(thickness,color);
//        }
    }

    @Override
    public void setJustifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
    }

    @Override
    public void setAlignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
    }

    //    @Override
//    public void setPadding(Integer padding) {
//        this.padding = padding;
//    }
}
