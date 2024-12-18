package io.github.lhcyh.lhmybatis.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String name;
    private List<Table> tableList;
    //private String basePackage;
    private String pojoPackage;
    private String entityPackage;
    private String mapperPackage;

    public Table getTableByName(String tableName){
        for(Table table:tableList){
            if(table.getName().equals(tableName)){
                return table;
            }
        }
        return null;
    }

    public List<Table> getAssociatedListByTableName(String tableName){
        List<Table> associatedList=new ArrayList<>();
        for(Table tableItem:tableList){
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

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
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
