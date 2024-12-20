package io.github.lhcyh.lhmybatis;

public enum Order{
    ASC("asc"),
    DESC("desc");
    private String value;
    Order(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}
