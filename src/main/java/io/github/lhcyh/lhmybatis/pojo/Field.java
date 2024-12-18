package io.github.lhcyh.lhmybatis.pojo;

import io.github.lhcyh.lhmybatis.utils.Utils;

import java.io.Serializable;

public class Field implements Serializable {
    public enum Type{
        VARCHAR("varchar"),
        INT("int"),
        DATETIME("datetime"),
        FLOAT("float"),
        BIT("bit"),
        ENUM("enum");
        private String value;
        Type(String value){
            this.value=value;
        }
        public String getValue(){
            return this.value;
        }

        public static Type getTypeByValue(String value){
            for(Type type: Type.values()){
                if(type.value.equals(value)){
                    return type;
                }
            }
            return null;
        }
    }
    private String name;
    private Type type;
    private Integer size;
    private boolean allowNull;
    private boolean isUnique;
    private boolean isPrimaryKey;

    public String getJavaType(){
        switch (type){
            case VARCHAR:
                return "String";
            case INT:
                return "Integer";
            case BIT:
                return "Boolean";
            case FLOAT:
                return "Float";
            case DATETIME:
                return "Date";
            case ENUM:
                return Utils.underscoreToCamel(name,true);
        }
        return null;
    }

    public String getIfTag(String fill){
        String tag=fill+"<if test=\""+ Utils.underscoreToCamel(name,false)+"!=null\">\n"+
                fill+"   `"+name+"`=#{"+ Utils.underscoreToCamel(name,false)+"},\n"+
                fill+"</if>";
        return tag;
    }

    public String getJavaProperty(String fill){
        return fill+"private "+getJavaType()+" "+ Utils.underscoreToCamel(name,false)+";";
    }

    public String getJavaListProperty(String fill){
        return fill+"private List<"+getJavaType()+"> "+ Utils.underscoreToCamel(name,false)+"List;";
    }

    public String getJavaGet(String fill){
        String head=" get";
        if(type== Type.BIT){
            head=" is";
        }
        String javaGet=fill+"public "+getJavaType()+head+ Utils.underscoreToCamel(name,true)+"(){\n";
        javaGet+=fill+"  "+"return "+ Utils.underscoreToCamel(name,false)+";\n";
        javaGet+=fill+"}";
        return javaGet;
    }

    public String getJavaSet(String fill){
        String javaSet=fill+"public void set"+ Utils.underscoreToCamel(name,true)+"("+getJavaType()+" "+ Utils.underscoreToCamel(name,false)+"){\n";
        javaSet+=fill+"  "+"this."+ Utils.underscoreToCamel(name,false)+"="+ Utils.underscoreToCamel(name,false)+";\n";
        javaSet+=fill+"}";
        return javaSet;
    }

    public String getJavaListGet(String fill){
        String javaListGet=fill+"public List<"+getJavaType()+"> get"+ Utils.underscoreToCamel(name,true)+"List(){\n";
        javaListGet+=fill+"  "+"return "+ Utils.underscoreToCamel(name,false)+"List;\n";
        javaListGet+=fill+"}";
        return javaListGet;
    }

    public String getJavaListSet(String fill){
        String javaListSet=fill+"public void set"+ Utils.underscoreToCamel(name,true)+"List(List<"+getJavaType()+"> "+ Utils.underscoreToCamel(name,false)+"){\n";
        javaListSet+=fill+"  "+"this."+ Utils.underscoreToCamel(name,false)+"List="+ Utils.underscoreToCamel(name,false)+"List;\n";
        javaListSet+=fill+"}";
        return javaListSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }
}
