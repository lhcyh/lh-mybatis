package io.github.lhcyh.lhmybatis.assistant.generator.utils;

import io.github.lhcyh.lhmybatis.assistant.generator.pojo.ForeignKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String name;
    private List<TableHandle> tableList;
    //private String basePackage;
    private String pojoPackage;
    private String entityPackage;
    private String mapperPackage;

    public TableHandle getTableByName(String tableName){
        for(TableHandle table:tableList){
            if(table.getName().equals(tableName)){
                return table;
            }
        }
        return null;
    }

    /**
     * 根据表名获取被联系的表数组
     * @param tableName
     * @return
     */
    public List<TableHandle> getAssociatedListByTableName(String tableName){
        List<TableHandle> associatedList=new ArrayList<>();
        for(TableHandle tableItem:tableList){
            if(tableItem.getForeignKeyList()!=null){
                for(ForeignKey foreignKeyItem:tableItem.getForeignKeyList()){
                    if (foreignKeyItem.getReferencedTableName().equals(tableName)) {
                        associatedList.add(tableItem);
                        break;
                    }
                }
            }
        }
        return associatedList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TableHandle> getTableList() {
        return tableList;
    }

    public void setTableList(List<TableHandle> tableList) {
        this.tableList = tableList;
    }

    public String getPojoPackage() {
        return pojoPackage;
    }

    public void setPojoPackage(String pojoPackage) {
        this.pojoPackage = pojoPackage;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }
}
