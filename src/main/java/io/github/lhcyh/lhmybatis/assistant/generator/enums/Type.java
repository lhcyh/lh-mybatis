package io.github.lhcyh.lhmybatis.assistant.generator.enums;

public enum Type {
    VARCHAR("varchar","String"),
    INT("int","Integer"),
    DATETIME("datetime","Date"),
    FLOAT("float","Float"),
    BIT("bit","Boolean"),
    ENUM("enum",null),
    LONGTEXT("longtext","String"),
    TINYINT("tinyint","Integer"),
    TIMESTAMP("timestamp","Date");
    private String mysqlType;
    private String javaType;
    Type(String mysqlType,String javaType){
        this.mysqlType=mysqlType;
        this.javaType=javaType;
    }

    public static Type getTypeByMysqlTypeValue(String mysqlType){
        for(Type type:Type.values()){
            if(type.mysqlType.equals(mysqlType)){
                return type;
            }
        }
        return null;
    }

    public String getMysqlType() {
        return mysqlType;
    }

    public String getJavaType() {
        return javaType;
    }
}
