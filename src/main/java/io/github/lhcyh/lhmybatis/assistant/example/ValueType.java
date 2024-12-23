package io.github.lhcyh.lhmybatis.assistant.example;

public enum ValueType{
    NoValue("noValue"),
    SingleValue("singleValue"),
    BetweenValue("betweenValue"),
    ListValue("listValue");
    private String value;
    ValueType(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}
