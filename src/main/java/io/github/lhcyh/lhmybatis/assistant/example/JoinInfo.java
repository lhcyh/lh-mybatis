package io.github.lhcyh.lhmybatis.assistant.example;

import java.util.Objects;

/**
 * 连表信息
 */
public class JoinInfo{
    private String leftTable;
    private String leftKey;
    private String rightTable;
    private String rightKey;

    public JoinInfo(String leftTable, String leftKey, String rightTable, String rightKey) {
        this.leftTable = leftTable;
        this.leftKey = leftKey;
        this.rightTable = rightTable;
        this.rightKey = rightKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinInfo joinInfo = (JoinInfo) o;
        return leftTable.equals(joinInfo.leftTable) &&
                leftKey.equals(joinInfo.leftKey) &&
                rightTable.equals(joinInfo.rightTable) &&
                rightKey.equals(joinInfo.rightKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftTable, leftKey, rightTable, rightKey);
    }

    public String getLeftTable() {
        return leftTable;
    }

    public String getLeftKey() {
        return leftKey;
    }

    public String getRightTable() {
        return rightTable;
    }

    public String getRightKey() {
        return rightKey;
    }
}
