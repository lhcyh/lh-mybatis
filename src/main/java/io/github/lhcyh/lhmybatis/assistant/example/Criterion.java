package io.github.lhcyh.lhmybatis.assistant.example;

/**
 * 准则
 */
public class Criterion{
    private String prefix;
//    private String table;
//    private String field;
    private String attribute;
    private Object value;
    private Object secondValue;
    private String valueType;
    private String condition;

//    public String getTable() {
//        return table;
//    }
//
//    public void setTable(String table) {
//        this.table = table;
//    }
//
//    public String getField() {
//        return field;
//    }
//
//    public void setField(String field) {
//        this.field = field;
//    }


    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Object getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(Object secondValue) {
        this.secondValue = secondValue;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}