package io.github.lhcyh.lhmybatis.assistant.generator.utils;

import io.github.lhcyh.lhmybatis.assistant.generator.enums.Type;
import io.github.lhcyh.lhmybatis.assistant.generator.pojo.ForeignKey;

import java.io.Serializable;
import java.util.List;

public class TableHandle implements Serializable {
    private String name;
    private List<Field> fieldList;
    private List<ForeignKey> foreignKeyList;
//    @JsonIgnore
//    private List<Table> associatedList;

    private Field getPrimaryKey(){
        for(Field fieldItem:fieldList){
            if(fieldItem.getIsPrimaryKey()){
                return fieldItem;
            }
        }
        return null;
    }

    private ForeignKey getForeignKeyByFieldName(String fieldName){
        for(ForeignKey foreignKey:foreignKeyList){
            if(foreignKey.getFieldName().equals(fieldName)){
                return foreignKey;
            }
        }
        return null;
    }

    private Field getFieldByName(String fieldName){
        for(Field fieldItem:fieldList){
            if(fieldItem.getName().equals(fieldName)){
                return fieldItem;
            }
        }
        return null;
    }

//    //通过表名获取联系的外键
//    public ForeignKey getAssociateForeignKeyByTableName(String tableName){
//        for(ForeignKey foreignKey:foreignKeyList){
//            if(foreignKey.getReferencedTableName().equals(tableName)){
//                return foreignKey;
//            }
//        }
//        return null;
//    }

    public String getFileName(String suffix){
        return Utils.underscoreToCamel(name,true)+suffix;
    }

    public String getPojoCode(Project project){
        String pojoCode ="package "+project.getPojoPackage()+";\n\n";
        pojoCode+="public class "+getFileName("")+"{\n";
        String propertyList="";
        String getSetList="";
        for(Field field:fieldList){
            propertyList+=field.getJavaProperty("    ")+"\n";
            getSetList+=field.getJavaGet("    ")+"\n\n";
            getSetList+=field.getJavaSet("    ")+"\n\n";
        }

//        for(ForeignKey foreignKey:foreignKeyList){
//            if(foreignKey.getAssociate()==ForeignKey.Associate.OneToOneL){
//                propertyList+=CodeUtils.getPropertyByTableName(foreignKey.getReferencedTableName(),"    ")+"\n";
//                getSetList+=CodeUtils.getJavaListGetByTableName(foreignKey.getReferencedTableName(),"   ")+"\n";
//                getSetList+=CodeUtils.getJavaSetByTableName(foreignKey.getReferencedTableName(),"   ")+"\n";
//            }
//        }

//        for (Table tableItem:associatedList){
//            switch (tableItem.getAssociateForeignKeyByTableName(name).getAssociate()){
//                case OneToOneR:
//                    propertyList+=CodeUtils.getListPropertyByTableName(tableItem.getName(),"    ")+"\n";
//                    getSetList+=CodeUtils.getJavaGetByTableName(tableItem.getName(),"   ")+"\n";
//                    getSetList+=CodeUtils.getJavaSetByTableName(tableItem.getName(),"   ")+"\n";
//                    break;
//                case ManyToOne:
//                    propertyList+=CodeUtils.getListPropertyByTableName(tableItem.getName(),"    ")+"\n";
//                    getSetList+=CodeUtils.getJavaListGetByTableName(tableItem.getName(),"   ")+"\n";
//                    getSetList+=CodeUtils.getJavaListSetByTableName(tableItem.getName(),"   ")+"\n";
//                    break;
//            }
//        }
        return pojoCode+propertyList+"\n"+getSetList+"}";
    }

    public String getEntityCode(Project project){
        List<TableHandle> associatedList=project.getAssociatedListByTableName(name);
        if((foreignKeyList==null||foreignKeyList.size()==0)&&(associatedList.size()==0)){
            return null;
        }
        String entityCodeHead="package "+project.getEntityPackage()+";\n\n";
        entityCodeHead+="import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n";
        entityCodeHead+="import "+project.getPojoPackage()+"."+getFileName("")+";\n";
        String entityCode="@JsonIgnoreProperties(value={\"handler\"})\n";
        entityCode+="public class "+getFileName("Entity")+" extends "+getFileName("")+"{\n";
        String lp="";
        String entityGetSet="";
        if(foreignKeyList!=null) {
            for (ForeignKey foreignKey : foreignKeyList) {
                if (foreignKey.getAssociate() == ForeignKey.Associate.OneToOneL) {
                    lp="import io.github.lhcyh.lhmybatis.LeftJoin;\n";
                    String upCamelName = Utils.underscoreToCamel(foreignKey.getReferencedTableName(), true);
                    String doCamelName = Utils.underscoreToCamel(foreignKey.getReferencedTableName(), false);
                    String entity = "";
                    if (project.getTableByName(foreignKey.getReferencedTableName()).getEntityCode(project) != null) {
                        entity = "Entity";
//                        entityCodeHead += "import " + project.getEntityPackage() + "." + upCamelName + "Entity;\n";
                    } else {
                        entityCodeHead += "import " + project.getPojoPackage() + "." + upCamelName + ";\n";
                    }
                    entityCode += "     @LeftJoin(leftKey=\""+foreignKey.getFieldName()+"\",rightKey=\""+foreignKey.getReferencedFieldName()+"\")\n";
                    entityCode += "     private " + upCamelName + entity + " " + doCamelName + ";\n";
                    entityGetSet += "     public " + upCamelName + entity + " get" + upCamelName + "(){\n";
                    entityGetSet += "          return " + doCamelName + ";\n";
                    entityGetSet += "     }\n\n";
                    entityGetSet += "     public void set" + upCamelName + "(" + upCamelName + entity + " " + doCamelName + "){\n";
                    entityGetSet += "          this." + doCamelName + "=" + doCamelName + ";\n";
                    entityGetSet += "     }\n\n";
                }
            }
        }

        for (TableHandle table : associatedList) {
            String upCamelName = Utils.underscoreToCamel(table.getName(), true);
            String doCamelName = Utils.underscoreToCamel(table.getName(), false);
            String entity = "";
            String iPackage="";
            boolean iTag=false;

            for(ForeignKey foreignKey:table.getForeignKeyList()){
                if(!foreignKey.getReferencedTableName().equals(name)){
                    continue;
                }
                if(foreignKey.getAssociate()== ForeignKey.Associate.OneToOneL){
                    continue;
                }
                if (table.getEntityCode(project) != null) {
                    entity = "Entity";
//                    iPackage= "import " + project.getEntityPackage() + "." + upCamelName + "Entity;\n";
                } else {
                    iPackage= "import " + project.getPojoPackage() + "." + upCamelName + ";\n";
                }
                iTag=true;
                switch (foreignKey.getAssociate()) {
                    case OneToOneR:
                        lp="import io.github.lhcyh.lhmybatis.LeftJoin;\n";
                        entityCode += "    private " + upCamelName + entity + "  " + doCamelName + ";\n";
                        entityGetSet += "    public " + upCamelName + entity + " get" + upCamelName + "(){\n";
                        entityGetSet += "        return " + doCamelName + ";\n";
                        entityGetSet += "    }\n\n";
                        entityGetSet += "    public void set" + upCamelName + "(" + upCamelName + entity + " " + doCamelName + "){\n";
                        entityGetSet += "        this." + doCamelName + "=" + doCamelName + ";\n";
                        entityGetSet += "    }\n\n";
                        break;
                    case ManyToOne:
                        entityCode += "    private " + "List<" + upCamelName + entity + ">  " + doCamelName + "List;\n";
                        entityGetSet += "    public " + "List<" + upCamelName + entity + "> get" + upCamelName + "List(){\n";
                        entityGetSet += "        return " + doCamelName + "List;\n";
                        entityGetSet += "    }\n\n";
                        entityGetSet += "    public void set" + upCamelName + "List(List<" + upCamelName + entity + "> " + doCamelName + "List){\n";
                        entityGetSet += "        this." + doCamelName + "List=" + doCamelName + "List;\n";
                        entityGetSet += "    }\n\n";
                        break;
                }
            }
            if(iTag){
                entityCodeHead+=iPackage;
            }
        }
        entityCodeHead+=lp;
        if(entityCode.contains("List")){
            entityCodeHead+="import java.util.List;\n";
        }
        if(entityGetSet.equals("")){
            return null;
        }else {
            return entityCodeHead+"\n"+entityCode+"\n"+entityGetSet+"}\n";
        }
    }

    public String getMapperCode(Project project){
        String upName= Utils.underscoreToCamel(name,true);
        String doName= Utils.underscoreToCamel(name,false);
        String primaryKeyType=getPrimaryKey().getJavaType();
        String primaryKeyUpName= Utils.underscoreToCamel(getPrimaryKey().getName(),true);
        String primaryKeyDoName= Utils.underscoreToCamel(getPrimaryKey().getName(),false);
        String mapperCode= "package "+ project.getMapperPackage()+";\n\n";
        mapperCode+="import io.github.lhcyh.lhmybatis.Example;\n";
        mapperCode+="import "+project.getPojoPackage()+"."+upName+";\n";
        String entity="";
        if(getEntityCode(project)!=null){
            entity="Entity";
            mapperCode+="import "+project.getEntityPackage()+"."+upName+"Entity;\n";
        }

        mapperCode+="import java.util.List;\n";
        mapperCode+="import org.apache.ibatis.annotations.Mapper;\n\n";
        mapperCode+="@Mapper\n";
        mapperCode+="public interface "+upName+"Mapper{\n";
        mapperCode+="    public int add"+upName+"("+upName+" "+doName+");\n";
        mapperCode+="    public int delete"+upName+"By"+primaryKeyUpName+"("+primaryKeyType+" "+primaryKeyDoName+");\n";
        mapperCode+="    public int update"+upName+"("+upName+" "+doName+");\n";
        for(Field fieldItem:fieldList){
            if(fieldItem.getIsUnique()){
                String fUpName= Utils.underscoreToCamel(fieldItem.getName(),true);
                String fDoName= Utils.underscoreToCamel(fieldItem.getName(),false);
                mapperCode+="    public "+upName+entity+" get"+upName+"By"+fUpName+"("+fieldItem.getJavaType()+" "+fDoName+");\n";
            }
        }
        if(foreignKeyList!=null) {
            for (ForeignKey foreignKeyItem : foreignKeyList) {
                if (foreignKeyItem.getAssociate() == ForeignKey.Associate.ManyToOne) {
                    String fUpName = Utils.underscoreToCamel(foreignKeyItem.getFieldName(), true);
                    String fDoName = Utils.underscoreToCamel(foreignKeyItem.getFieldName(), false);
                    Field field = getFieldByName(foreignKeyItem.getFieldName());
                    mapperCode += "    public List<" + upName+entity + "> get" + upName + "ListBy" + fUpName + "(" + field.getJavaType() + " " + fDoName + ");\n";
                }
            }
        }
        mapperCode+="    public List<"+upName+entity+"> get"+upName+"ListByExample(Example<"+upName+"> example);\n";
        mapperCode+="}\n";
        return mapperCode;
    }

    public String getServiceCode(Project project){
        String upName= Utils.underscoreToCamel(name,true);
        String doName= Utils.underscoreToCamel(name,false);
        String primaryKeyType=getPrimaryKey().getJavaType();
        String primaryKeyUpName= Utils.underscoreToCamel(getPrimaryKey().getName(),true);
        String primaryKeyDoName= Utils.underscoreToCamel(getPrimaryKey().getName(),false);
        String tName=null;
        if(getEntityCode(project)!=null){
            tName=upName+"Entity";
        }else {
            tName=upName;
        }
        String serviceCode="@Service\n";
        serviceCode+="public class "+upName+"Service{\n";
        serviceCode+="    @Autowired\n";
        serviceCode+="    private "+upName+"Mapper "+doName+"Mapper;\n\n";

        serviceCode+="    public int add"+upName+"("+upName+" "+doName+"){\n";
        serviceCode+="        return "+doName+"Mapper.add"+upName+"("+doName+");\n";
        serviceCode+="    }\n\n";

        serviceCode+="    public int delete"+upName+"By"+primaryKeyUpName+"("+primaryKeyType+" "+primaryKeyDoName+"){\n";
        serviceCode+="        return "+doName+"Mapper.delete"+upName+"By"+primaryKeyUpName+"("+primaryKeyDoName+");\n";
        serviceCode+="    }\n\n";

        serviceCode+="    public int update"+upName+"("+upName+" "+doName+"){\n";
        serviceCode+="        return "+doName+"Mapper.update"+upName+"("+doName+");\n";
        serviceCode+="    }\n\n";

        for(Field fieldItem:fieldList){
            if(fieldItem.getIsUnique()){
                String fUpName= Utils.underscoreToCamel(fieldItem.getName(),true);
                String fDoName= Utils.underscoreToCamel(fieldItem.getName(),false);
                serviceCode+="    public "+upName+" get"+upName+"By"+fUpName+"("+fieldItem.getJavaType()+" "+fDoName+"){\n";
                serviceCode+="        return "+doName+"Mapper.get"+upName+"By"+fUpName+"("+fDoName+");\n";
                serviceCode+="    }\n\n";
            }
        }

        serviceCode+="    public List<"+tName+"> get"+upName+"ListByExample(Example<"+upName+"> example){\n";
        serviceCode+="        return "+doName+"Mapper.get"+upName+"ListByExample(example);\n";
        serviceCode+="    }\n";
        serviceCode+="}\n";
        return serviceCode;
    }

    public String getControllerCode(){
        String upName= Utils.underscoreToCamel(name,true);
        String doName= Utils.underscoreToCamel(name,false);
        String controllerCode="@RestController\n";
        controllerCode+="@RequestMapping(\"/"+doName+"\")\n";
        controllerCode+="public class "+upName+"Controller{\n";
        controllerCode+="}\n";
        return controllerCode;
    }

    private String getInsertXml(String packageName,String fill){
        String upName= Utils.underscoreToCamel(name,true);
        String xmlHead=fill+"<insert id=\"add"+upName+"\"";
        String xml=" parameterType=\""+packageName+"."+upName+"\">\n";
        xml+=fill+"     insert into `"+name+"`(";
        String tableValue=null;
        String beanValue=null;
        for (Field fieldItem:fieldList){
            String lupName=Utils.underscoreToCamel(fieldItem.getName(),false);
            if(fieldItem.getIsPrimaryKey()&&fieldItem.getType()== Type.INT){
                xmlHead+=" useGeneratedKeys=\"true\" keyProperty=\""+lupName+"\"";
                continue;
            }
            if(tableValue==null){
                tableValue="`"+fieldItem.getName()+"`";
                beanValue="#{"+ lupName +"}";
            }else {
                tableValue+=",`"+fieldItem.getName()+"`";
                beanValue+=",#{"+ lupName +"}";
            }
        }
        xml=xmlHead+xml;
        xml+=tableValue+") values("+beanValue+")\n";
        xml+=fill+"</insert>\n";
        return xml;
    }

    private String getDeleteXml(String fill){
        String pKeyName=getPrimaryKey().getName();
        String pDoName= Utils.underscoreToCamel(pKeyName,false);
        String pUpName= Utils.underscoreToCamel(pKeyName,true);
        String upName= Utils.underscoreToCamel(name,true);
        String xml=fill+"<delete id=\"delete"+upName+"By"+pUpName+"\">\n";
        xml+=fill+"     delete from `"+name+"` where "+pKeyName+"=#{"+pDoName+"}\n";
        xml+=fill+"</delete>\n";
        return xml;
    }

    private String getUpdateXml(String packageName,String fill){
        String upName= Utils.underscoreToCamel(name,true);
        String pKeyName=getPrimaryKey().getName();
        String pDoName= Utils.underscoreToCamel(pKeyName,false);
        String xml=fill+"<update id=\"update"+upName+"\" parameterType=\""+packageName+"."+upName+"\">\n";
        xml+=fill+"    update `"+name+"`\n";
        xml+=fill+"    <set>\n";
        for(Field fieldItem:fieldList){
            if(fieldItem.getIsPrimaryKey()){
                continue;
            }else {
                xml+=fieldItem.getIfTag(fill+"    "+"    ")+"\n";
            }
        }
        xml+=fill+"    </set>\n";
        xml+=fill+"    where "+pKeyName+"=#{"+pDoName+"}\n";
        xml+=fill+"</update>\n";
        return xml;
    }

    private String getResultXml(Project project){
        String upName= Utils.underscoreToCamel(name,true);
        String entity=getEntityCode(project);
        String result=" resultMap=\"baseMap\" ";
        if(entity==null){
            result=" resultType=\""+project.getPojoPackage()+"."+upName+"\" ";
        }
        return result;
    }

    private String getGetByUniqueFieldXml(String fill,Project project){
        String upName= Utils.underscoreToCamel(name,true);
        String result=getResultXml(project);
        String xml="";
        for(Field fieldItem:fieldList){
            if(fieldItem.getIsUnique()){
                String fUpName= Utils.underscoreToCamel(fieldItem.getName(),true);
                String fDoName= Utils.underscoreToCamel(fieldItem.getName(),false);
                xml+=fill+"<select id=\"get"+upName+"By"+fUpName+"\""+result+">\n";
                xml+=fill+"    select * from `"+name+"` where `"+fieldItem.getName()+"`=#{"+fDoName+"}\n";
                xml+=fill+"</select>\n";
            }
        }
        return xml;
    }

    private String getGetListByForeignKeyXml(String fill,Project project){
        String xml="";
        String upName= Utils.underscoreToCamel(name,true);
        String result=getResultXml(project);
        if(foreignKeyList!=null) {
            for (ForeignKey foreignKeyItem : foreignKeyList) {
                String fUpName = Utils.underscoreToCamel(foreignKeyItem.getFieldName(), true);
                String fDoName = Utils.underscoreToCamel(foreignKeyItem.getFieldName(), false);
                switch (foreignKeyItem.getAssociate()) {
                    case ManyToOne:
                        xml += fill + "<select id=\"get" + upName + "ListBy" + fUpName + "\" " + result + ">\n";
                        xml += fill + "    select * from `" + name + "` where " + foreignKeyItem.getFieldName() + "=#{" + fDoName + "}\n";
                        xml += fill + "</select>\n";
                        break;
                }
            }
        }
        return xml;
    }

//    private String getGetBeanXml(String fill){
//        String xml="<select id=\"get"+CodeUtils.underscoreToCamel(name,true)+"\" resultMap=\"aMap\">\n";
//        xml+="   <trim prefix=\"where\" prefixOverrides=\"and\">\n";
//        for(Field fieldItem:fieldList){
//            xml+=fieldItem.getIfTag("      ")+"\n";
//        }
//        xml+=fill+"   </trim>\n";
//        xml+=fill+"</select>";
//        return xml;
//    }
//
    private String getBaseMapXml(String fill,Project project){
        if(getEntityCode(project)==null){
            return "";
        }
        String upName= Utils.underscoreToCamel(name,true);
        String pName=getPrimaryKey().getName();
        String pDoName= Utils.underscoreToCamel(pName,false);
        String xml="";
        xml+=fill+"<resultMap id=\"baseMap\" type=\""+project.getEntityPackage()+"."+upName+"Entity\">\n";
        xml+=fill+"    <id property=\""+pDoName+"\" column=\""+pName+"\"/>\n";
        if(foreignKeyList!=null) {
            for (ForeignKey foreignKeyItem : foreignKeyList) {
                if (foreignKeyItem.getAssociate() == ForeignKey.Associate.OneToOneL) {
                    String fDoName = Utils.underscoreToCamel(foreignKeyItem.getFieldName(), false);
                    String rtUpName = Utils.underscoreToCamel(foreignKeyItem.getReferencedTableName(), true);
                    String rtDoName = Utils.underscoreToCamel(foreignKeyItem.getReferencedTableName(), false);
                    String rtpUpName = Utils.underscoreToCamel(project.getTableByName(foreignKeyItem.getReferencedTableName()).getPrimaryKey().getName(), true);
//                String rtEntity=project.getTableByName(foreignKeyItem.getReferencedTableName()).getEntityCode(project);
//                String javaType=null;
//                if(rtEntity==null){
//                    javaType=project.getBasePackage()+".pojo."+rtUpName;
//                }else {
//                    javaType=project.getBasePackage()+".entity."+rtUpName+"Entity";
//                }
                    xml += fill + "    <result property=\"" + fDoName + "\" column=\"" + foreignKeyItem.getFieldName() + "\"/>\n";
                    xml += fill + "    <association fetchType=\"lazy\" property=\"" + rtDoName + "\" column=\"" + foreignKeyItem.getFieldName() + "\" select=\"" + project.getMapperPackage() + "." + rtUpName + "Mapper." + "get" + rtUpName + "By" + rtpUpName + "\"/>\n";
                }

            }
        }
        List<TableHandle> associatedList=project.getAssociatedListByTableName(name);
        for(TableHandle tableItem:associatedList){
//            ForeignKey associateForeignKey=tableItem.getAssociateForeignKeyByTableName(name);
            String tUpName= Utils.underscoreToCamel(tableItem.getName(),true);
            String tDoName= Utils.underscoreToCamel(tableItem.getName(),false);
            for(ForeignKey foreignKey:tableItem.getForeignKeyList()) {
                if(!foreignKey.getReferencedTableName().equals(name)){
                    continue;
                }
                if(foreignKey.getAssociate()== ForeignKey.Associate.OneToOneL){
                    continue;
                }
                String tfUpName = Utils.underscoreToCamel(foreignKey.getFieldName(), true);
                switch (foreignKey.getAssociate()) {
                    case OneToOneR:
                        xml += fill + "    <association fetchType=\"lazy\" property=\"" + tDoName + "\" column=\"" + foreignKey.getReferencedFieldName() +
                                "\" select=\"" + project.getMapperPackage() + "." + tUpName + "Mapper.get" + tUpName + "By" + tfUpName + "\"/>\n";
                        break;
                    case ManyToOne:
                        xml += fill + "    <collection fetchType=\"lazy\" property=\"" + tDoName + "List\" column=\"" + foreignKey.getReferencedFieldName() +
                                "\" select=\"" + project.getMapperPackage() + "." + tUpName + "Mapper.get" + tUpName + "ListBy" + tfUpName + "\"/>\n";
                        break;
                }
            }
        }
        xml+=fill+"</resultMap>\n";
        return xml;
    }

    private String getGetListByExampleXml(Project project,String fill){
        String upName= Utils.underscoreToCamel(name,true);
        String xml=fill+"<select id=\"get"+upName+"ListByExample\" parameterType=\""+"io.github.lhcyh.lhmybatis.Example\" "+getResultXml(project)+">\n";
        xml+=fill+"    select * from `"+name+"`\n";
        xml+=fill+"    <include refid=\"Common.exampleClause\"></include>\n";
        xml+=fill+"</select>\n";
        return xml;
    }

    public String getXml(Project project){
        String upName= Utils.underscoreToCamel(name,true);
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml+="<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n";
        xml+="<mapper namespace=\""+ project.getMapperPackage()+ "."+ upName+ "Mapper\">\n";
        xml+=getInsertXml(project.getPojoPackage(),"    ")+"\n";
        xml+=getDeleteXml("    ")+"\n";
        xml+=getUpdateXml(project.getPojoPackage(),"    ")+"\n";
        xml+=getBaseMapXml("    ",project)+"\n";
        xml+=getGetByUniqueFieldXml("    ",project)+"\n";
        xml+=getGetListByForeignKeyXml("    ",project)+"\n";
//        xml+=getTableFieldMapping("    ",project)+"\n";
//        String leftJoin=getLeftJoinXml("    ",project);
//        if(leftJoin!=null){
//            xml+=leftJoin+"\n";
//        }
//        xml+=getExampleClause("    ",project)+"\n";
        //xml+=getExampleClause()+"\n";
        xml+=getGetListByExampleXml(project,"    ")+"\n";
        xml+="</mapper>\n";
        return  xml;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }

    public List<ForeignKey> getForeignKeyList() {
        return foreignKeyList;
    }

    public void setForeignKeyList(List<ForeignKey> foreignKeyList) {
        this.foreignKeyList = foreignKeyList;
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", fieldList=" + fieldList +
                ", foreignKeyList=" + foreignKeyList +
//                ", associatedList=" + associatedList +
                '}';
    }
}
