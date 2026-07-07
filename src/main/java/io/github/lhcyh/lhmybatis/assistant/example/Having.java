package io.github.lhcyh.lhmybatis.assistant.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Having<Model> extends ConditionParenthesisClause<Model> {
    private ConditionClause sumCondition;
    private ConditionClause countCondition;
    private ConditionClause avgCondition;
    private ConditionClause maxCondition;
    private ConditionClause minCondition;
    private Set<String> selectList;
//    private String gbAttribute;

    public Having(Set<JoinInfo> joinInfo){
        super(new ArrayList<>(),joinInfo);
        selectList=new HashSet<>();
    }

    private String getTagByAttribute(String attribute){
        String tag=attribute.replaceAll("`","");
        tag=tag.replaceAll("\\.","_");
        return tag;
    }

    private String handleAttribute(String agg,String attribute){
        String tag=agg.toLowerCase()+"_"+getTagByAttribute(attribute);
        attribute="COALESCE("+agg+"("+attribute+"),0)";
        selectList.add(attribute+" AS "+tag);
        return attribute;
    }

//    private void handleJoinInfo(JoinInfo joinInfo){
//        if(gbAttribute==null&&joinInfo!=null){
//            gbAttribute="`"+joinInfo.getLeftTable()+"`."+joinInfo.getLeftKey();
//        }
//    }

    public ConditionClause<Model> sum(){
        if(sumCondition==null){
            sumCondition=new ConditionClause<Model>(getCriterionList(),getJoinInfoList()){
//                @Override
//                protected JoinInfo getJoinInfo(Class tClass, Field field) {
//                    JoinInfo joinInfo= super.getJoinInfo(tClass, field);
//                    handleJoinInfo(joinInfo);
//                    return joinInfo;
//                }

                @Override
                protected String getAttribute(Class tClass, Field field) {
                    String attribute=super.getAttribute(tClass, field);
                    return handleAttribute("SUM",attribute);
                }
            };
        }
        return sumCondition;
    }

    public ConditionClause<Model> count(){
        if(countCondition==null){
            countCondition=new ConditionClause<Model>(getCriterionList(),getJoinInfoList()){
//                @Override
//                protected JoinInfo getJoinInfo(Class tClass, Field field) {
//                    JoinInfo joinInfo= super.getJoinInfo(tClass, field);
//                    handleJoinInfo(joinInfo);
//                    return joinInfo;
//                }

                @Override
                protected String getAttribute(Class tClass, Field field) {
                    String attribute= super.getAttribute(tClass, field);
                    return handleAttribute("COUNT",attribute);
                }
            };
        }
        return countCondition;
    }

    public ConditionClause<Model> avg(){
        if(avgCondition==null){
            avgCondition=new ConditionClause(getCriterionList(),getJoinInfoList()){
//                @Override
//                protected JoinInfo getJoinInfo(Class tClass, Field field) {
//                    JoinInfo joinInfo= super.getJoinInfo(tClass, field);
//                    handleJoinInfo(joinInfo);
//                    return joinInfo;
//                }

                @Override
                protected String getAttribute(Class tClass, Field field) {
                    String attribute= super.getAttribute(tClass, field);
                    return handleAttribute("AVG",attribute);
                }
            };
        }
        return avgCondition;
    }

    public ConditionClause<Model> max(){
        if(maxCondition==null){
            maxCondition=new ConditionClause(getCriterionList(),getJoinInfoList()){
//                @Override
//                protected JoinInfo getJoinInfo(Class tClass, Field field) {
//                    JoinInfo joinInfo= super.getJoinInfo(tClass, field);
//                    handleJoinInfo(joinInfo);
//                    return joinInfo;
//                }

                @Override
                protected String getAttribute(Class tClass, Field field) {
                    String attribute= super.getAttribute(tClass, field);
                    return handleAttribute("MAX",attribute);
                }
            };
        }
        return maxCondition;
    }

    public ConditionClause<Model> min(){
        if(minCondition==null){
            minCondition=new ConditionClause(getCriterionList(),getJoinInfoList()){
//                @Override
//                protected JoinInfo getJoinInfo(Class tClass, Field field) {
//                    JoinInfo joinInfo= super.getJoinInfo(tClass, field);
//                    handleJoinInfo(joinInfo);
//                    return joinInfo;
//                }

                @Override
                protected String getAttribute(Class tClass, Field field) {
                    String attribute= super.getAttribute(tClass, field);
                    return handleAttribute("MIN",attribute);
                }
            };
        }
        return minCondition;
    }

    public Set<String> getSelectList() {
        return selectList;
    }

//    public void setGbAttribute(String gbAttribute) {
//        this.gbAttribute = gbAttribute;
//    }
//
//    public String getGbAttribute() {
//        return gbAttribute;
//    }
}
