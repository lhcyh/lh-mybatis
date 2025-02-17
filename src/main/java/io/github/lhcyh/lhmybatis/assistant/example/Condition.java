package io.github.lhcyh.lhmybatis.assistant.example;

public enum Condition{
    EqualTo("=", ValueType.SingleValue),
    NotEqualTo("!=", ValueType.SingleValue),
    GreaterThan(">", ValueType.SingleValue),
    LessThan("<", ValueType.SingleValue),
    LessThanOrEqualTo("<=", ValueType.SingleValue),
    Between("between", ValueType.BetweenValue),
    IsNull("is null", ValueType.NoValue),
    IsNotNull("is not null", ValueType.NoValue),
    Like("like", ValueType.SingleValue),
    NotLike("not like", ValueType.SingleValue),
    In("in", ValueType.ListValue);
    private String value;
    private ValueType valueType;
    Condition(String value, ValueType valueType){
        this.value=value;
        this.valueType=valueType;
    }

    public String getValue() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }
}
