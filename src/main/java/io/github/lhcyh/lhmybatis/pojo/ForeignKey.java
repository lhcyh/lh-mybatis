package io.github.lhcyh.lhmybatis.pojo;

import java.io.Serializable;

public class ForeignKey implements Serializable {
    public enum Associate{
        OneToOneR("1->1"),
        OneToOneL("1<-1"),
        ManyToOne("m-1");
        private String value;

        Associate(String value){
            this.value=value;
        }

        public String getValue(){
            return value;
        }
    }
    private String name;
    private String fieldName;
    private String referencedTableName;
    private String referencedFieldName;
    private Associate associate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public String getReferencedFieldName() {
        return referencedFieldName;
    }

    public void setReferencedFieldName(String referencedFieldName) {
        this.referencedFieldName = referencedFieldName;
    }

    public Associate getAssociate() {
        return associate;
    }

    public void setAssociate(Associate associate) {
        this.associate = associate;
    }
}
