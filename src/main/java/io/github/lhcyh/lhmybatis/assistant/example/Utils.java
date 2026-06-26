package io.github.lhcyh.lhmybatis.assistant.example;

import java.util.List;

public class Utils {
    public static String handlePrefix(List<Criterion> criterionList, Prefix prefix){
        if(prefix==null){
            return null;
        }
        if(criterionList.size()>0){
            Criterion criterion=criterionList.get(criterionList.size()-1);
            Condition condition=Condition.getConditionByValue(criterion.getCondition());
            if(condition==Condition.LeftParenthesis){
                return null;
            }else {
                return prefix.name();
            }
        }
        return prefix.name();
    }
}
