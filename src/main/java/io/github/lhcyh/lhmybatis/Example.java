package io.github.lhcyh.lhmybatis;

import io.github.lhcyh.lhmybatis.assistant.example.*;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 样板
 * @param <Model>
 */
public class Example<Model> extends ConditionParenthesisClause<Model>{
    /** 排序准则 **/
    private List<Criterion> orderList;
//    /** 分组准则 **/
//    private List<Criterion> groupList;
//    private List<Criterion> havingList;
    private Having<Model> having;
    /** sql语句的limit的起始行数，从0开始 **/
    private Integer limitStart;
    /** 要查询的条数 **/
    private Integer limitNum;
    private Object extend;

    public Example(){
        super(new ArrayList<>(),new HashSet<>());
    }

    /**
     * 载入查询准则
     * @param model 属性值模板
     * @param pValue 自定义值
     */
    private void loadCriterion(List<Criterion> list,Object model,String pValue){
        if(model==null){
            return;
        }
        List<Field> fieldList=getFieldList(model);
        for(Field field:fieldList){
            Object value=getValue(model,field);
            if(value==null){
                continue;
            }
            JoinInfo joinInfo=getJoinInfo(model.getClass(),field);
            if(joinInfo!=null){
                loadCriterion(list,value,pValue);
                getJoinInfoList().add(joinInfo);
            }else {
                Criterion criterion=createCriterion(null,model.getClass(),field,null);
                criterion.setValue(pValue);
                list.add(criterion);
            }
        }
    }

    /**
     * 根据model里不为null的属性排序，排序方式默认为ASC（正序）
     * @param model
     * @return
     */
    public void orderBy(Model model){
        orderBy(model,Order.ASC);
    }

    /**
     * 根据model里不为null的属性排序
     * @param model
     * @param order 排序方式，ASC（正序），DESC（倒序）
     * @return
     */
    public void orderBy(Model model,Order order){
        if(orderList==null){
            orderList=new ArrayList<>();
        }
        loadCriterion(orderList,model,order.name());
    }

    public Having<Model> having(){
        if(having==null){
            having=new Having<>(getJoinInfoList());
        }
        return having;
    }

    private AggOrderBy aggHandle(Having<Model> tempHaving){
        this.having();
        if(tempHaving.getGbAttribute()!=null) {
            having.setGbAttribute(tempHaving.getGbAttribute());
            having.getSelectList().addAll(tempHaving.getSelectList());
        }
        AggOrderBy aggOrderBy=new AggOrderBy() {
            @Override
            public void orderBy(Order order) {
                for(Criterion criterion:tempHaving.getCriterionList()){
                    criterion.setValue(order.name());
                    if(orderList==null){
                        orderList=new ArrayList<>();
                    }
                    orderList.add(criterion);
                }
            }
        };
        return aggOrderBy;
    }

    public AggOrderBy sum(Model model){
        Having<Model> tempHaving=new Having<>(getJoinInfoList());
        tempHaving.sum().andEqualTo(model);
        return aggHandle(tempHaving);
    }

    public AggOrderBy count(Model model){
        Having<Model> tempHaving=new Having<>(getJoinInfoList());
        tempHaving.count().andEqualTo(model);
        return aggHandle(tempHaving);
    }

    public AggOrderBy avg(Model model){
        Having<Model> tempHaving=new Having<>(getJoinInfoList());
        tempHaving.avg().andEqualTo(model);
        return aggHandle(tempHaving);
    }

    public AggOrderBy max(Model model){
        Having<Model> tempHaving=new Having<>(getJoinInfoList());
        tempHaving.max().andEqualTo(model);
        return aggHandle(tempHaving);
    }



//    public Having<Model> groupBy(Model model){
//        if(groupList==null){
//            groupList=new ArrayList<>();
//        }
//        loadCriterion(groupList,model,null);
//        if(having==null){
//            havingList=new ArrayList<>();
//            having=new Having<>(havingList,getJoinInfoList());
//        }
//        return having;
//    }

    /**
     * 限制查询范围
     * @param num 返回查询结果的前 num 条记录
     * @return
     */
    public Example<Model> limit(Integer num){
        this.limitStart=null;
        this.limitNum=num;
        return this;
    }

    /**
     * 限制查询范围
     * @param start start参数表示从第几行开始查，起始数为0
     * @param num num表示要查询的行数
     * @return
     */
    public Example<Model> limit(Integer start,Integer num){
        if(num==null){
            return this;
        }
        this.limitStart=start;
        this.limitNum=num;
        return this;
    }

    public void setExtend(Object extend) {
        this.extend = extend;
    }

    public Object getExtend() {
        return extend;
    }

//    public List<Criterion> getCriterionList() {
//        return criterionList;
//    }

    public List<Criterion> getOrderList() {
        return orderList;
    }

//    public Set<JoinInfo> getLeftJoinList() {
//        return leftJoinList;
//    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public List<Criterion> getHavingList() {
        if(having!=null&&having.getCriterionList().size()>0){
            return having.getCriterionList();
        }
        return null;
    }

//    public String getAggregateSelect(){
//        if(having==null){
//            return "";
//        }else {
//            return having.getAggregateSelect();
//        }
//    }

    public String getAggregateSelect(){
        if(having==null){
            return "";
        }
        StringBuilder select=new StringBuilder();
        for(String sl:having.getSelectList()){
            select.append(",");
            select.append(sl);
        }
        return select.toString();
    }

    public String getGbAttribute(){
        if(having==null){
            return null;
        }
        return having.getGbAttribute();
    }
}