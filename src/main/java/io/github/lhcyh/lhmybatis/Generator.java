package io.github.lhcyh.lhmybatis;

import io.github.lhcyh.lhmybatis.pojo.*;
import io.github.lhcyh.lhmybatis.utils.MybatisFactory;
import io.github.lhcyh.lhmybatis.utils.Utils;
import io.github.lhcyh.lhswing.*;
import io.github.lhcyh.lhswing.AlignItems;
import io.github.lhcyh.lhswing.FlexDirection;
import io.github.lhcyh.lhswing.JustifyContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Generator{
    /** 文字尺寸1 **/
    private int tSize1=27;
    /** 文字尺寸2 **/
    private int tSize2=tSize1-4;
    /** 文字尺寸3 **/
    private int tSize3=tSize2-4;

    private LhBody lhBody;
    private LhDiv rootDiv;
    private Connection connection;
    private Profile profile;

    public Generator(){
        this.lhBody=new LhBody("代码生成器");
        this.rootDiv=lhBody.getDiv();
        this.rootDiv.setJustifyContent(JustifyContent.CENTER);
        this.rootDiv.setAlignItems(AlignItems.CENTER);
        this.createConnectPanel();
    }

    private void connectDatabase(String url,String username,String password,String driverClass){
        try {
            // 加载并注册JDBC驱动类
            Class.forName(driverClass);
            // 建立数据库连接
            this.connection = DriverManager.getConnection(url, username, password);

//            Profile profile=this.getProfile();
//            if(profile==null){
//                profile=new Profile();
//            }
            profile.setUrl(url);
            profile.setUsername(username);
            profile.setPassword(password);
            profile.setDriverClass(driverClass);
            this.setProfile(profile);
            System.out.println("connectSuccess");
            this.createSelectPanel();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"连接失败，请检查连接信息");
        }
    }

    private void createConnectPanel(){
//        LhDiv contentDiv=new LhDiv();
//        contentDiv.setFlexDirection(FlexDirection.COLUMN);
//        contentDiv.setJustifyContent(JustifyContent.CENTER);
//        contentDiv.setAlignItems(AlignItems.CENTER);

        LhTable lhTable=new LhTable(10);
        int inputWidth=400;

        LhRow row1=lhTable.addRow();
        LhLabel url=new LhLabel("url:");
        url.setFontSize(tSize3);
        row1.addComponent(url);
        LhInput urlInput=new LhInput();
        urlInput.setFontSize(tSize3);
        urlInput.setWidth(inputWidth);
        urlInput.setText("jdbc:mysql://localhost:3306/database_name?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
        row1.addComponent(urlInput);

        LhRow row2=lhTable.addRow();
        LhLabel username=new LhLabel("username:");
        username.setFontSize(tSize3);
        row2.addComponent(username);
        LhInput usernameInput=new LhInput();
        usernameInput.setFontSize(tSize3);
        usernameInput.setWidth(inputWidth);
        usernameInput.setText("root");
        row2.addComponent(usernameInput);

        LhRow row3=lhTable.addRow();
        LhLabel password=new LhLabel("password:");
        password.setFontSize(tSize3);
        row3.addComponent(password);
        LhInput passwordInput=new LhInput();
        passwordInput.setFontSize(tSize3);
        passwordInput.setWidth(inputWidth);
        row3.addComponent(passwordInput);

        LhRow row4=lhTable.addRow();
        LhLabel driverClass=new LhLabel("driverClass:");
        driverClass.setFontSize(tSize3);
        row4.addComponent(driverClass);
        LhInput driverClassInput=new LhInput();
        driverClassInput.setFontSize(tSize3);
        driverClassInput.setWidth(inputWidth);
        driverClassInput.setText("com.mysql.cj.jdbc.Driver");
        row4.addComponent(driverClassInput);

        this.profile=getProfile();
        if(profile!=null){
            urlInput.setText(profile.getUrl());
            usernameInput.setText(profile.getUsername());
            passwordInput.setText(profile.getPassword());
            driverClassInput.setText(profile.getDriverClass());
        }else {
            profile=new Profile();
        }

        LhButton button=new LhButton("连接数据库");
        button.setFontSize(tSize3);
        button.setPadding(20);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(urlInput.getText().equals("")){
                    JOptionPane.showMessageDialog(null,"请输入数据库地址");
                    return;
                }
                if(passwordInput.getText().equals("")){
                    JOptionPane.showMessageDialog(null,"请输入数据库密码");
                    return;
                }
                if(usernameInput.getText().equals("")){
                    JOptionPane.showMessageDialog(null,"请输入用户名");
                    return;
                }
                if(driverClassInput.getText().equals("")){
                    JOptionPane.showMessageDialog(null,"请输入数据库驱动");
                    return;
                }
                button.setEnabled(false);
                button.setText("连接中...");
                connectDatabase(urlInput.getText(),usernameInput.getText(),passwordInput.getText(),driverClassInput.getText());
                button.setEnabled(true);
                button.setText("连接数据库");
            }
        });

        this.rootDiv.setFlexDirection(FlexDirection.COLUMN);
        this.rootDiv.setJustifyContent(JustifyContent.SPACE_AROUND);
        this.rootDiv.add(lhTable);
        this.rootDiv.add(button);
    }

    private void createSelectPanel(){
        this.rootDiv.removeAll();
        LhDiv contentDiv=new LhDiv();
        this.rootDiv.add(contentDiv);
        contentDiv.setWidthPercent(0.8f);
        contentDiv.setHeightPercent(1f);
        contentDiv.setFlexDirection(FlexDirection.COLUMN);
        contentDiv.setAlignItems(AlignItems.CENTER);
        contentDiv.setJustifyContent(JustifyContent.SPACE_AROUND);

        LhDiv select=new LhDiv();
        contentDiv.add(select);
        select.setFlexDirection(FlexDirection.COLUMN);
        select.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        select.setPadding(10);
        select.setWidthPercent(1f);
        select.setHeightPercent(0.8f);

        LhLabel sTitle=new LhLabel("选择表");
        sTitle.setFontSize(tSize1);
        select.add(sTitle);

        LhCheckBox allCheck=new LhCheckBox("全选");
        allCheck.setFontSize(tSize2);
        select.add(allCheck);

        LhScrollPane lhScrollPane=new LhScrollPane();
        select.add(lhScrollPane);
        lhScrollPane.setWidthPercent(0.9f);
        lhScrollPane.setHeightPercent(0.7f);
        LhDiv scrDiv=lhScrollPane.getDiv();
        scrDiv.setFlexDirection(FlexDirection.COLUMN);

        Project project=this.profile.getProject();
        if(project==null){
            project=new Project();
            profile.setProject(project);
        }
        List<Table> tableList=project.getTableList();
        if(tableList==null){
            tableList=new ArrayList<>();
            project.setTableList(tableList);
        }
        List<Table> dTableList= Utils.getTableList(this.connection);
        /** 将保存的表的外键联系赋值给新的表 **/
        for(Table table:tableList){
            for(Table dTable:dTableList){
                if(table.getName().equals(dTable.getName())){
                    dTable.setForeignKeyList(table.getForeignKeyList());
                }
            }
        }

        List<Table> tempTableList=new ArrayList<>();
        List<LhCheckBox> checkBoxList=new ArrayList<>();
        for(Table table:dTableList){
            LhCheckBox cTable=new LhCheckBox(table.getName());
            cTable.setFontSize(tSize3);
            checkBoxList.add(cTable);
            //cTable.setSelected(true);
            scrDiv.add(cTable);
            cTable.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        tempTableList.add(table);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        tempTableList.remove(table);
                    }
                }
            });
            for(Table pTable:tableList){
                if(pTable.getName().equals(table.getName())){
                    //tempTableList.add(table);
                    cTable.setSelected(true);
                }
            }
        }
        project.setTableList(tempTableList);

        allCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    for(LhCheckBox checkBox:checkBoxList){
                        checkBox.setSelected(true);
                    }
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    for(LhCheckBox checkBox:checkBoxList){
                        checkBox.setSelected(false);
                    }
                }
            }
        });

        LhButton nextButton=new LhButton("下一步");
        nextButton.setFontSize(tSize3);
        contentDiv.add(nextButton);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tempTableList.size()==0){
                    JOptionPane.showMessageDialog(null,"请选择表");
                    return;
                }
                createGeneratorInfoPanel();
            }
        });
    }

    /**
     * 初始化选择弹窗
     * @param dialog
     * @param confirmButton
     * @return
     */
    private LhDiv initSelectDialog(LhDialog dialog,LhButton confirmButton){
        LhDiv dialogDiv=dialog.getDiv();
        dialogDiv.setJustifyContent(JustifyContent.SPACE_AROUND);
        dialogDiv.setFlexDirection(FlexDirection.COLUMN);

        LhScrollPane lhScrollPane=new LhScrollPane();
        dialogDiv.add(lhScrollPane);
        lhScrollPane.setWidthPercent(0.9f);
        lhScrollPane.setHeightPercent(0.6f);
        LhDiv content=lhScrollPane.getDiv();
        content.setFlexDirection(FlexDirection.COLUMN);
        LhDiv bContent=new LhDiv();
        bContent.setJustifyContent(JustifyContent.CENTER);
        dialogDiv.add(bContent);
        bContent.add(confirmButton);
        return content;
    }

    /**
     * 创建参考表选择弹窗
     * @param exTable
     * @param tableList
     * @param foreignKey
     * @param clickButton
     */
    private void createSelectRTableDialog(Table exTable, List<Table> tableList, ForeignKey foreignKey, LhButton clickButton, LhButton selectRFieldButton){
        LhDialog dialog=new LhDialog(this.lhBody,"选择参考表");
        LhButton confirm=new LhButton("确定");
        confirm.setFontSize(tSize3);
        LhDiv content=initSelectDialog(dialog,confirm);

        Table sTable=new Table();
        ButtonGroup buttonGroup=new ButtonGroup();
        for(Table table:tableList){
            if(table==exTable){
                continue;
            }
            LhRadio radio=new LhRadio(table.getName());
            radio.setFontSize(tSize3);
            radio.setPadding(20);
            buttonGroup.add(radio);
            content.add(radio);
            if(foreignKey.getReferencedTableName()!=null){
                if(foreignKey.getReferencedTableName().equals(table.getName())){
                    radio.setSelected(true);
                }
            }
            radio.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange()==ItemEvent.SELECTED){
                        sTable.setName(table.getName());
                        sTable.setFieldList(table.getFieldList());
                    }
                }
            });
        }

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sTable.getName()!=null){
                    foreignKey.setReferencedTableName(sTable.getName());
                    clickButton.setText(sTable.getName());
                    selectRFieldButton.removeAllActionListener();
                    selectRFieldButton.setEnabled(true);
                    selectRFieldButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            createSelectRFieldDialog(sTable.getFieldList(),foreignKey,selectRFieldButton);
                        }
                    });
                }
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * 创建外键选择弹窗
     * @param fieldList
     * @param foreignKey
     * @param clickButton
     */
    private void createSelectFKeyDialog(List<Field> fieldList, ForeignKey foreignKey, LhButton clickButton){
        LhDialog dialog=new LhDialog(this.lhBody,"选择外键");
        LhButton confirm=new LhButton("确定");
        confirm.setFontSize(tSize3);
        LhDiv content=initSelectDialog(dialog,confirm);

        Field sField=new Field();
        ButtonGroup buttonGroup=new ButtonGroup();
        for(Field field:fieldList){
            if(field.getIsPrimaryKey()){
                continue;
            }
            LhRadio radio=new LhRadio(field.getName());
            radio.setFontSize(tSize3);
            radio.setPadding(20);
            buttonGroup.add(radio);
            content.add(radio);
            if(foreignKey.getFieldName()!=null){
                if(foreignKey.getFieldName().equals(field.getName())){
                    radio.setSelected(true);
                }
            }
            radio.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange()==ItemEvent.SELECTED){
                        sField.setName(field.getName());
                    }
                }
            });
        }

        //content.add(confirm);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sField.getName()!=null){
                    foreignKey.setFieldName(sField.getName());
                    clickButton.setText(sField.getName());
                }
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * 创建参考列选择弹窗
     * @param fieldList
     * @param foreignKey
     * @param clickButton
     */
    private void createSelectRFieldDialog(List<Field> fieldList,ForeignKey foreignKey,LhButton clickButton){
        LhDialog dialog=new LhDialog(this.lhBody,"选择参考列");
        LhButton confirm=new LhButton("确定");
        confirm.setFontSize(tSize3);
        LhDiv content=initSelectDialog(dialog,confirm);

        Field sField=new Field();
        ButtonGroup buttonGroup=new ButtonGroup();

        for(Field field:fieldList){
            LhRadio radio=new LhRadio(field.getName());
            radio.setFontSize(tSize3);
            radio.setPadding(20);
            buttonGroup.add(radio);
            content.add(radio);
            if(foreignKey.getFieldName()!=null){
                if(foreignKey.getFieldName().equals(field.getName())){
                    radio.setSelected(true);
                }
            }
            radio.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange()==ItemEvent.SELECTED){
                        sField.setName(field.getName());
                    }
                }
            });
        }

        //content.add(confirm);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sField.getName()!=null){
                    foreignKey.setReferencedFieldName(sField.getName());
                    clickButton.setText(sField.getName());
                }
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    /**
     * 创建联系选择弹窗
     * @param foreignKey
     * @param clickButton
     */
    private void createSelectAssociate(ForeignKey foreignKey,LhButton clickButton){
        LhDialog dialog=new LhDialog(this.lhBody,"选择联系");
        LhButton confirm=new LhButton("确定");
        confirm.setFontSize(tSize3);
        LhDiv content=initSelectDialog(dialog,confirm);
        ForeignKey temp=new ForeignKey();
        ButtonGroup buttonGroup=new ButtonGroup();
        for(ForeignKey.Associate associate: ForeignKey.Associate.values()){
            LhRadio radio=new LhRadio(associate.getValue());
            radio.setFontSize(tSize3);
            radio.setPadding(20);
            content.add(radio);
            buttonGroup.add(radio);
            if(foreignKey.getAssociate()!=null){
                if(foreignKey.getAssociate()==associate){
                    radio.setSelected(true);
                }
            }
            radio.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange()==ItemEvent.SELECTED){
                        temp.setAssociate(associate);
                    }
                }
            });
        }

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(temp.getAssociate()!=null){
                    foreignKey.setAssociate(temp.getAssociate());
                    clickButton.setText(temp.getAssociate().getValue());
                }
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private void addRowByForeignKey(ForeignKey foreignKey,LhTable lhTable,Table tableData,List<Table> tableList){
        LhRow newRow=lhTable.addRow();
        //tableData.getForeignKeyList().add(foreignKey);

        LhButton wj=new LhButton("···");
        wj.setFontSize(tSize3);
        if(foreignKey.getFieldName()!=null){
            wj.setText(foreignKey.getFieldName());
        }
        newRow.addComponent(wj);
        wj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //wj.setText("这是一个测试的按钮");
                createSelectFKeyDialog(tableData.getFieldList(),foreignKey,wj);
            }
        });

        LhButton ckb=new LhButton("···");
        LhButton ckl=new LhButton("···");
        ckb.setFontSize(tSize3);
        ckl.setFontSize(tSize3);
        ckl.setEnabled(false);

        if(foreignKey.getReferencedTableName()!=null){
            ckb.setText(foreignKey.getReferencedTableName());
            ckl.setEnabled(true);
        }
        newRow.addComponent(ckb);

        if(foreignKey.getReferencedFieldName()!=null){
            ckl.setText(foreignKey.getReferencedFieldName());
        }
        newRow.addComponent(ckl);

        ckb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSelectRTableDialog(tableData,tableList,foreignKey,ckb,ckl);
            }
        });

        LhButton lx=new LhButton("···");
        lx.setFontSize(tSize3);
        if(foreignKey.getAssociate()!=null){
            lx.setText(foreignKey.getAssociate().getValue());
        }
        newRow.addComponent(lx);

        lx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSelectAssociate(foreignKey,lx);
            }
        });

        LhButton delete=new LhButton("删除");
        delete.setFontSize(tSize3);
        newRow.addComponent(delete);

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "确定删除此外键联系？", "确认删除？", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    tableData.getForeignKeyList().remove(foreignKey);
                    lhTable.removeRow(newRow);
                }
            }
        });
    }

    private void createCenterContent(Table tableData,LhDiv parent){
        //Table tableData=this.profile.getProject().getTableList().get(tableIndex);
        List<Table> tableList=this.profile.getProject().getTableList();
        if(tableData.getForeignKeyList()==null){
            tableData.setForeignKeyList(new ArrayList<>());
        }
        parent.removeAll();
        LhDiv head=new LhDiv();
        parent.add(head);
        head.setAlignItems(AlignItems.CENTER);
        LhLabel tableName=new LhLabel(tableData.getName());
        tableName.setFontSize(tSize1);
        tableName.setPadding(10);
        head.add(tableName);
        LhButton addButton=new LhButton("+添加外键联系");
        addButton.setFontSize(tSize3);
        head.add(addButton);

        LhScrollPane centerScroll=new LhScrollPane();
        parent.add(centerScroll);
        centerScroll.setWidthPercent(0.9f);
        centerScroll.setHeightPercent(0.8f);

        LhDiv centerScrollDiv=centerScroll.getDiv();
        LhTable table=new LhTable();
        table.setBorder(1,Color.GRAY);
        table.setAlignItems(AlignItems.CENTER);
        table.setJustifyContent(JustifyContent.CENTER);
        centerScrollDiv.add(table);

        LhRow hRow=table.addRow();
        String[] titles={"外键","参考表","参考列","联系","操作"};
        for(int i=0;i<titles.length;i++){
            LhLabel title=new LhLabel(titles[i]);
            title.setFontSize(tSize2);
            hRow.addComponent(title);
        }
//        LhLabel filed=new LhLabel("外键");
//        hRow.addComponent(filed);
//        LhLabel referencedTable=new LhLabel("参考表");
//        hRow.addComponent(referencedTable);
//        LhLabel referencedField=new LhLabel("参考列");
//        hRow.addComponent(referencedField);
//        LhLabel associate=new LhLabel("联系");
//        hRow.addComponent(associate);
//        LhLabel operate=new LhLabel("操作");
//        hRow.addComponent(operate);

        for(ForeignKey foreignKey:tableData.getForeignKeyList()){
            addRowByForeignKey(foreignKey,table,tableData,tableList);
        }

        //Generator that=this;
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ForeignKey foreignKey=new ForeignKey();
                addRowByForeignKey(foreignKey,table,tableData,tableList);
                tableData.getForeignKeyList().add(foreignKey);

//                LhRow newRow=table.addRow();
//                ForeignKey foreignKey=new ForeignKey();
//                tableData.getForeignKeyList().add(foreignKey);
//                LhButton wj=new LhButton("···");
//                newRow.addComponent(wj);
//                wj.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        //wj.setText("这是一个测试的按钮");
//                        createSelectFKeyDialog(tableData.getFieldList(),foreignKey,wj);
//                    }
//                });
//
//                LhButton ckb=new LhButton("···");
//                newRow.addComponent(ckb);
//
//                LhButton ckl=new LhButton("···");
//                newRow.addComponent(ckl);
//                ckl.setEnabled(false);
//
//                ckb.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        createSelectRTableDialog(tableData,tableList,foreignKey,ckb,ckl);
//                    }
//                });
//
//                LhButton lx=new LhButton("···");
//                newRow.addComponent(lx);
//
//                lx.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        createSelectAssociate(foreignKey,lx);
//                    }
//                });
//
//                LhButton delete=new LhButton("删除");
//                newRow.addComponent(delete);
//
//                delete.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        int option = JOptionPane.showConfirmDialog(null, "确定删除此外键联系？", "确认删除？", JOptionPane.YES_NO_OPTION);
//                        if (option == JOptionPane.YES_OPTION) {
//                            tableData.getForeignKeyList().remove(foreignKey);
//                            table.removeRow(newRow);
//                        }
//                    }
//                });
            }
        });

        //List<ForeignKey> foreignKeyList=this.profile.getProject().getTableList().get(tableIndex).getForeignKeyList();
    }

    private Boolean checkForeignKey(List<ForeignKey> foreignKeyList){
        Set<String> fSet=new HashSet<>();
        for(ForeignKey foreignKey:foreignKeyList){
            if(foreignKey.getFieldName()==null){
                JOptionPane.showMessageDialog(null,"请选择外键");
                return false;
            }
            if(foreignKey.getReferencedTableName()==null){
                JOptionPane.showMessageDialog(null,"请选择参考表");
                return false;
            }
            if(foreignKey.getReferencedFieldName()==null){
                JOptionPane.showMessageDialog(null,"请选择参考列");
                return false;
            }
            if(foreignKey.getAssociate()==null){
                JOptionPane.showMessageDialog(null,"请选择外键联系");
                return false;
            }
            Boolean tag=fSet.add(foreignKey.getFieldName()+"_" + foreignKey.getReferencedTableName()+"_"+foreignKey.getReferencedFieldName()+"_"+foreignKey.getAssociate());
            if(!tag){
                JOptionPane.showMessageDialog(null,"外键联系重复，请删除");
                return false;
            }
        }
        return true;
    }

    private void createGeneratorInfoPanel(){
        this.rootDiv.removeAll();
        LhDiv contentDiv=new LhDiv();
        //contentDiv.setBorder(2,Color.black);
        this.rootDiv.add(contentDiv);
        contentDiv.setWidthPercent(0.9f);
        contentDiv.setHeightPercent(1f);
        contentDiv.setFlexDirection(FlexDirection.COLUMN);
        contentDiv.setAlignItems(AlignItems.CENTER);

        LhDiv tContent=new LhDiv();
        LhDiv bContent=new LhDiv();
        contentDiv.add(tContent);
        contentDiv.add(bContent);
        tContent.setPadding(10);
        tContent.setWidthPercent(1f);
        tContent.setHeightPercent(0.8f);
        tContent.setJustifyContent(JustifyContent.SPACE_AROUND);
        bContent.setWidthPercent(1f);
        bContent.setHeightPercent(0.2f);
        bContent.setJustifyContent(JustifyContent.SPACE_AROUND);

        LhButton lastButton=new LhButton("上一步");
        lastButton.setFontSize(tSize3);
        LhButton submitButton=new LhButton("提交");
        submitButton.setFontSize(tSize3);
        bContent.add(lastButton);
        bContent.add(submitButton);

        lastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSelectPanel();
            }
        });

        LhDiv left=new LhDiv();
        left.setFlexDirection(FlexDirection.COLUMN);
        tContent.add(left);
        left.setWidthPercent(0.25f);
        left.setHeightPercent(1f);

        LhScrollPane leftScroll=new LhScrollPane();
        left.add(leftScroll);
        leftScroll.setWidthPercent(0.9f);
        leftScroll.setHeightPercent(0.9f);

        LhDiv leftScrollDiv=leftScroll.getDiv();
        leftScrollDiv.setFlexDirection(FlexDirection.COLUMN);
        ButtonGroup buttonGroup=new ButtonGroup();

        LhDiv center=new LhDiv();
        tContent.add(center);
        center.setPadding(10);
        center.setFlexDirection(FlexDirection.COLUMN);
        center.setWidthPercent(0.4f);
        center.setHeightPercent(1f);

        List<Table> tableList=this.profile.getProject().getTableList();
        List<LhRadio> radioList=new ArrayList<>();
        for(int i=0;i<tableList.size();i++){
            int index=i;
            Table table=tableList.get(i);
            LhRadio tableRadio=new LhRadio(table.getName());
            tableRadio.setFontSize(tSize3);
            buttonGroup.add(tableRadio);
            radioList.add(tableRadio);
            tableRadio.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        //tableName.setText(table.getName());
                        createCenterContent(tableList.get(index),center);
                    }
                }
            });
            leftScrollDiv.add(tableRadio);
            if(i==0){
                tableRadio.setSelected(true);
            }
        }
        this.createCenterContent(tableList.get(0),center);

        LhDiv right=new LhDiv();
        right.setPadding(15);
        right.setFlexDirection(FlexDirection.COLUMN);
        right.setBorder(1,Color.GRAY);
        tContent.add(right);
        right.setWidthPercent(0.25f);
        right.setHeightPercent(0.9f);

        String[] titles={"生成model的路径","生成entity的路径","生成mapper接口的路径"};
        String[] paths={"com.example.demo.pojo","com.example.demo.entity","com.example.demo.mapper"};
        String pojoPackage=profile.getProject().getPojoPackage();
        String entityPackage=profile.getProject().getEntityPackage();
        String mapperPackage=profile.getProject().getMapperPackage();
        if(pojoPackage!=null)
            paths[0]=pojoPackage;
        if(entityPackage!=null)
            paths[1]=entityPackage;
        if(mapperPackage!=null)
            paths[2]=mapperPackage;

        LhInput[] inputs=new LhInput[3];
        for(int i=0;i<titles.length;i++){
            LhDiv item=new LhDiv();
            item.setFlexDirection(FlexDirection.COLUMN);
            item.setPadding(5);
            LhLabel title=new LhLabel(titles[i]);
            title.setFontSize(tSize3);
            item.add(title);
            LhInput input=new LhInput();
            input.setWidth(250);
            input.setFontSize(tSize3);
            input.setText(paths[i]);
            item.add(input);
            right.add(item);
            inputs[i]=input;
        }


//        LhDiv item2=new LhDiv();
//        item2.setPadding(5);
//        LhLabel entity=new LhLabel("生成entity的路径");
//        item2.add(entity);
//        LhInput entityInput=new LhInput();
//        entityInput.setText("com.example.demo.entity");
//        item2.add(entityInput);
//
//        LhDiv item3=new LhDiv();
//        item3.setPadding(5);
//        LhLabel mapper=new LhLabel("生成mapper接口的路径");
//        item3.add(mapper);
//        LhInput mapperInput=new LhInput();
//        mapperInput.setText("com.example.demo.mapper");
//        item3.add(mapperInput);
//
//        right.add(item1);
//        right.add(item2);
//        right.add(item3);
//
//        String pojoPackage=profile.getProject().getPojoPackage();
//        String entityPackage=profile.getProject().getEntityPackage();
//        String mapperPackage=profile.getProject().getMapperPackage();
//        if(pojoPackage!=null)
//            pojoInput.setText(pojoPackage);
//        if(entityPackage!=null)
//            entityInput.setText(entityPackage);
//        if(mapperPackage!=null)
//            mapperInput.setText(mapperPackage);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i=0;i< titles.length;i++) {
                    if (inputs[i].getText().equals("")) {
                        JOptionPane.showMessageDialog(null, "请输入"+titles[i]);
                        return;
                    }
                }
//                if(entityInput.getText().equals("")){
//                    JOptionPane.showMessageDialog(null,"请输入生成entity路径");
//                    return;
//                }
//                if(mapperInput.getText().equals("")){
//                    JOptionPane.showMessageDialog(null,"请输入生成mapper路径");
//                    return;
//                }

                for(int i=0;i<tableList.size();i++){
                    if(!checkForeignKey(tableList.get(i).getForeignKeyList())){
                        radioList.get(i).setSelected(true);
                        return;
                    }
                }

                submitButton.setText("加载中...");
                submitButton.setEnabled(false);

                profile.getProject().setPojoPackage(inputs[0].getText());
                profile.getProject().setEntityPackage(inputs[1].getText());
                profile.getProject().setMapperPackage(inputs[2].getText());
                setProfile(profile);

                MybatisFactory mybatisFactory=new MybatisFactory(profile.getProject());
                ProjectCode projectCode=mybatisFactory.getProjectCode();
                String currentDir = System.getProperty("user.dir");
                String path=currentDir+"\\"+"src\\main\\java\\";

                List<CodeFile> codeFileList=projectCode.getPojo();
                String packagePath=profile.getProject().getPojoPackage().replace(".","\\");
                writeCodeFile(codeFileList,submitButton,path+packagePath,"model");

                packagePath=profile.getProject().getEntityPackage().replace(".","\\");
                codeFileList=projectCode.getEntity();
                writeCodeFile(codeFileList,submitButton,path+packagePath,"entity");

                packagePath=profile.getProject().getMapperPackage().replace(".","\\");
                codeFileList=projectCode.getMapper();
                writeCodeFile(codeFileList,submitButton,path+packagePath,"mapper");

                codeFileList=projectCode.getMapperXml();
                writeCodeFile(codeFileList,submitButton,currentDir+"\\src\\main\\resources\\"+packagePath,"mapperXml");

                JOptionPane.showMessageDialog(null,"已完成");
                submitButton.setText("提交");
                submitButton.setEnabled(true);
                System.out.println("success!");
            }
        });
    }

    private void writeCodeFile(List<CodeFile> codeFileList,LhButton submitButton,String path,String tag){
        boolean overwrite=false;
        for(CodeFile codeFile:codeFileList){
            //System.out.println(codeFile.getCode());
            String fileName=codeFile.getName();
            if(tag.equals("mapperXml")){
                fileName+=".xml";
            }else {
                fileName+=".java";
            }
            String filePath=path+"\\"+fileName;
            //System.out.println(filePath);
            File file = new File(filePath);
            if(!overwrite&&file.exists()){
                // 自定义按钮文本
                Object[] options = { "覆盖此文件", "覆盖所有"+tag, "跳过" };
                // 显示确认对话框
                int response = JOptionPane.showOptionDialog(null,
                        fileName+"已存在，是否覆盖文件？", // 提示信息
                        "是否覆盖", // 标题
                        JOptionPane.YES_NO_CANCEL_OPTION, // 选项类型
                        JOptionPane.QUESTION_MESSAGE, // 消息类型
                        null, // 图标
                        options, // 按钮文本
                        options[0]); // 默认选中的按钮

                // 根据用户的选择执行不同的操作
                switch (response) {
                    case JOptionPane.NO_OPTION:
                        // 用户选择了"全部覆盖"
                        overwrite=true;
                    case JOptionPane.YES_OPTION:
                        // 用户选择了"覆盖"
                        writeFile(file,codeFile.getCode());
                        continue;
                    case JOptionPane.CANCEL_OPTION:
                        // 用户选择了"跳过"
                        continue;
                }
            }else {
                writeFile(file,codeFile.getCode());
                continue;
            }
            submitButton.setText("提交");
            submitButton.setEnabled(true);
            return;
        }
    }

    private void writeFile(File file,String text){
        File parentDir = file.getParentFile();
        // 确保父目录存在
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.close();
            fw.close();
            System.out.println(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setProfile(Profile profile){
        // 将Map保存到文件
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("generator.profile"))) {
            out.writeObject(profile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Profile getProfile(){
        // 从文件中读取Map对象
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("generator.profile"))) {
            Profile restoredMap = (Profile) in.readObject();
            return restoredMap;
            //restoredMap.forEach((key, value) -> System.out.println(key + " -> " + value));
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            return null;
        }
    }

}
