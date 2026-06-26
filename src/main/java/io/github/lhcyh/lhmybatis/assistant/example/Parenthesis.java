package io.github.lhcyh.lhmybatis.assistant.example;

import java.util.List;

public class Parenthesis {
    private List<Criterion> criterionList;
    public Parenthesis(List<Criterion> criterionList){
        this.criterionList=criterionList;
    }

    /**
     * 载入查询准则
     * @param condition 判断条件（无表属性无值条件）
     */
    private void loadCriterion(Prefix prefix,Condition condition){
        Criterion criterion=new Criterion();
        criterion.setCondition(condition.getValue());
        criterion.setValueType(condition.getValueType().getValue());
        criterion.setPrefix(Utils.handlePrefix(criterionList,prefix));
        criterionList.add(criterion);
    }

    /**
     * 添加右括号
     * @return
     */
    public void rightParenthesis(){
        loadCriterion(null,Condition.RightParenthesis);
    }

    /**
     * 添加左括号
     * @return
     */
    public void orLeftParenthesis(){
        loadCriterion(Prefix.OR,Condition.LeftParenthesis);
    }

    /**
     * 添加左括号
     * @return
     */
    public void andLeftParenthesis(){
        loadCriterion(Prefix.AND,Condition.LeftParenthesis);
    }
}
