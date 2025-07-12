package io.github.lhcyh.lhmybatis.assistant.generator.enums;

public enum Type {
    VARCHAR("varchar","String"),
    CHAR("char","String"),
    TEXT("text","String"),
    LONGTEXT("longtext","String"),
    MEDIUMTEXT("mediumtext","String"),
    INT("int","Integer"),
    BIGINT("bigint","Long"),
    TINYINT("tinyint","Integer"),
    DATETIME("datetime","LocalDateTime"),
    FLOAT("float","Float"),
    DOUBLE("double","Double"),
    BIT("bit","Boolean"),
    ENUM("enum",null),
    TIMESTAMP("timestamp","LocalDateTime"),
    DECIMAL("decimal","BigDecimal");
    private String mysqlType;
    private String javaType;
    Type(String mysqlType, String javaType){
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
