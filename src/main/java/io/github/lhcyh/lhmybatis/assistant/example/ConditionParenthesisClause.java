package io.github.lhcyh.lhmybatis.assistant.example;

import java.util.List;
import java.util.Set;

public class ConditionParenthesisClause<Model> extends ConditionClause<Model>{
    public ConditionParenthesisClause(List<Criterion> criterionList, Set<JoinInfo> joinInfoSet) {
        super(criterionList, joinInfoSet);
    }

    /**
     * 载入查询准则
     * @param condition 判断条件（无表属性无值条件）
     */
    private void loadCriterion(Prefix prefix,Condition condition){
        Criterion criterion=new Criterion();
        criterion.setCondition(condition.getValue());
        criterion.setValueType(condition.getValueType().getValue());
        criterion.setPrefix(handlePrefix(prefix));
        getCriterionList().add(criterion);
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
