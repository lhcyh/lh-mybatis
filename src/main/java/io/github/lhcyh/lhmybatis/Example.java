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
public class Example<Model> {
    /** 准则数组 **/
    private List<Criterion> criterionList;
    /** 要连接的表名数组 **/
    private Set<JoinInfo> leftJoinList;
    /** sql语句的limit的起始行数，从0开始 **/
    private Integer limitStart;
    /** 要查询的条数 **/
    private Integer limitNum;
    /** 排序准则 **/
    private List<Criterion> orderList;
    private String extend;

    public Example(){
        criterionList=new ArrayList<>();
        leftJoinList=new HashSet<>();
    }

    /**
     * 获取get方法
     * @param model
     * @param field
     * @return
     */
    private Method getGetMethod(Object model, Field field) {
        if (model == null || field == null) {
            throw new IllegalArgumentException("Model and field cannot be null");
        }

        String fieldName = field.getName();

        // 确定标准方法名（首字母大写）
        String standardMethodName;
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            // 布尔类型使用 is 前缀
            standardMethodName = "is" + capitalize(fieldName);
        } else {
            // 其他类型使用 get 前缀
            standardMethodName = "get" + capitalize(fieldName);
        }

        try {
            // 首先尝试标准的首字母大写形式
            return model.getClass().getMethod(standardMethodName);
        } catch (NoSuchMethodException e) {
            // 如果标准形式失败，再尝试直接在字段名前加前缀（用于处理特殊情况，如 tSum -> gettSum）
            String directMethodName;
            if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                directMethodName = "is" + fieldName;
            } else {
                directMethodName = "get" + fieldName;
            }

            try {
                return model.getClass().getMethod(directMethodName);
            } catch (NoSuchMethodException ex) {
                // 两种方式都失败了，记录错误或抛出异常
                System.out.println("Getter method not found for field: " + fieldName +
                        ". Tried '" + standardMethodName + "' and '" + directMethodName + "'");
                return null; // 或者根据需要抛出异常
            }
        }
    }

    /**
     * 将字符串的首字母大写
     * @param str 输入字符串
     * @return 首字母大写的字符串
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 根据子类获取父类
     * @param subClass 子类
     * @return
     */
    private Class getInitialClass(Class subClass){
        if(subClass==Object.class){
            return subClass;
        }
        while (subClass.getSuperclass()!=Object.class){
            subClass=subClass.getSuperclass();
        }
        return subClass;
    }

    /**
     * 根据类获取表名
     * @param tClass
     * @return
     */
    private String getTableName(Class tClass){
        Class<?> iClass = getInitialClass(tClass);
        Table tableName=iClass.getAnnotation(Table.class);
        if(tableName!=null){
            return tableName.value();
        }
        return getUnderLineString(iClass.getSimpleName());
    }

    /**
     * 根据属性值获取表名
     * @param field
     * @return
     */
    private String getTableName(Field field){
        return getTableName(field.getType());
    }

    /**
     * 根据 Field 获取数据表中的列名
     * @param field
     * @return
     */
    private String getFiledName(Field field){
        Column fn=field.getAnnotation(Column.class);
        if(fn!=null){
            return fn.value();
        }
        String fieldName=field.getName();
        if(mapUnderscoreToCamelCase()){
            fieldName=getUnderLineString(fieldName);
        }
        return fieldName;
    }

    /**
     * 根据 Class、 Field 属性获取连表信息
     * @param tClass 左表类
     * @param field 左表类里的右表属性
     * @return 如果 field 属性为连表属性则返回连表信息，否则返回 null
     */
    private JoinInfo getJoinInfo(Class tClass,Field field){
        LeftJoin annotation=field.getAnnotation(LeftJoin.class);
        if(annotation==null){
            return null;
        }
        JoinInfo joinInfo=new JoinInfo(
                getTableName(tClass),
                annotation.leftKey(),
                getTableName(field),
                annotation.rightKey()
        );
        return joinInfo;
    }

//    /**
//     * 判断field属性是否为要连的表
//     * @param field
//     * @return
//     */
//    private Boolean isLeftJoinProperty(Field field){
////        if(model instanceof Enum){
////            return false;
////        }
//
//        if(field.getType().isEnum()){
//            return false;
//        }
//
//        if(field.getType().getClassLoader()==null){
//            return false;
//        }else {
//            return true;
//        }
//
//        // 获取包名
////        String modelPackage=model.getClass().getPackage().getName();
////        modelPackage=modelPackage.substring(0,modelPackage.lastIndexOf('.'));
////        String fieldPackage=field.getType().getPackage().getName();
////        fieldPackage=fieldPackage.substring(0,fieldPackage.lastIndexOf('.'));
////        if(modelPackage.equals(fieldPackage)){
////            return true;
////        }else {
////            return false;
////        }
//    }

    /**
     * 驼峰字符串转化为下划线字符串
     * @param string
     * @return
     */
    private String getUnderLineString(String string){
        String resString="";
        if(string==null){
            return null;
        }else {
            resString=string.substring(0,1).toLowerCase();
        }
        for(int i=1;i<string.length();i++){
            if(Character.isUpperCase(string.charAt(i))){
                resString+="_"+Character.toLowerCase(string.charAt(i));
            }else {
                resString+=string.charAt(i);
            }
        }
        return resString;
    }

    /**
     * 判断是否开启了驼峰映射
     * @return
     */
    private Boolean mapUnderscoreToCamelCase(){
        YamlPropertiesFactoryBean yaml=new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Properties properties = yaml.getObject();
        Boolean mapUnderscoreToCamelCase = (Boolean) properties.get("mybatis.configuration.map-underscore-to-camel-case");
        return mapUnderscoreToCamelCase;
    }

    /**
     * 获取属性列表
     * @param model
     * @return
     */
    private List<Field> getFieldList(Object model){
        Class modelClass=model.getClass();
        List<Field> fieldList=new ArrayList<>();
        while (true){
            /** 获取实体类的所有属性，返回Field数组 **/
            Field[] fields = modelClass.getDeclaredFields();
            for(int i=0;i<fields.length;i++){
                /** 判断是否为静态属性 **/
                if(Modifier.isStatic(fields[i].getModifiers())){
                    continue;
                }
                FieldIgnore fieldIgnore=fields[i].getAnnotation(FieldIgnore.class);
                if(fieldIgnore!=null){
                    continue;
                }
                fieldList.add(fields[i]);
            }
            if(modelClass.getSuperclass()==Object.class){
                break;
            }
            modelClass=modelClass.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 获取属性值
     * @param model
     * @param field 属性
     * @return
     */
    private Object getValue(Object model,Field field){
        Object value=null;
        Method m = getGetMethod(model,field);
        try {
            /** 调用getter方法获取属性值 **/
            value = m.invoke(model);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return value;
    }

    private String handlePrefix(Prefix prefix){
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

    /**
     * 创建查询准则
     * @param tClass
     * @param field
     * @param condition
     * @return
     */
    private Criterion createCriterion(Prefix prefix,Class tClass, Field field, Condition condition){
        Criterion criterion=new Criterion();
        String tableName=getTableName(tClass);
        criterion.setTable(tableName);
        String fieldName=getFiledName(field);
        criterion.setField(fieldName);
        criterion.setPrefix(handlePrefix(prefix));
        if(condition!=null){
            criterion.setCondition(condition.getValue());
            criterion.setValueType(condition.getValueType().getValue());
        }
        return criterion;
    }

    /**
     * 载入查询准则
     * @param condition 判断条件（无表属性无值条件）
     */
    private void loadCriterion(Prefix prefix,Condition condition){
        if(condition.getValueType()!= ValueType.NoValue){
            new Exception("Parameter error").printStackTrace();
            return;
        }
        Criterion criterion=new Criterion();
        criterion.setCondition(condition.getValue());
        criterion.setPrefix(handlePrefix(prefix));
    }

    /**
     * 载入查询准则
     * @param model 属性值模板
     * @param condition 判断条件（单值条件或无值条件）
     */
    private void loadCriterion(Prefix prefix,Object model, Condition condition){
        if(!(condition.getValueType()== ValueType.NoValue||condition.getValueType()== ValueType.SingleValue)){
            new Exception("Parameter error").printStackTrace();
            return;
        }
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
                leftJoinList.add(joinInfo);
                loadCriterion(prefix,value,condition);
            }else {
                Criterion criterion=createCriterion(prefix,model.getClass(),field,condition);
                criterion.setValue(value);
                criterionList.add(criterion);
            }
        }
    }

    /**
     * 载入查询准则
     * @param modelList 属性模板列表
     * @param condition 判断条件（多值条件）
     */
    private void loadCriterion(Prefix prefix,List<Object> modelList,Condition condition){
        if(!(condition.getValueType()== ValueType.ListValue)){
            new Exception("Parameter error").printStackTrace();
            return;
        }
        if(modelList==null||modelList.size()==0){
            return;
        }
        List<Field> fieldList=getFieldList(modelList.get(0));
        for(Field field:fieldList){
            List<Object> valueList=new ArrayList<>();
            for(Object model:modelList){
                Object value=getValue(model,field);
                if(value!=null){
                    valueList.add(value);
                }
            }
            if(valueList.size()>0){
                JoinInfo joinInfo=getJoinInfo(modelList.get(0).getClass(),field);
                if(joinInfo!=null){
                    loadCriterion(prefix,valueList,condition);
                    leftJoinList.add(joinInfo);
                }else {
                    Criterion criterion=createCriterion(prefix,modelList.get(0).getClass(),field,condition);
                    criterion.setValue(valueList);
                    criterionList.add(criterion);
                }
            }
        }
    }

    /**
     * 载入查询准则
     * @param model1 属性模板1
     * @param model2 属性模板2
     * @param condition 判断条件（between条件）
     */
    private void loadCriterion(Prefix prefix,Object model1,Object model2,Condition condition){
        if(!(condition.getValueType()== ValueType.BetweenValue)){
            new Exception("Parameter error").printStackTrace();
            return;
        }
        if(model1==null||model2==null){
            return;
        }
        List<Field> fieldList=getFieldList(model1);
        for(Field field:fieldList){
            Object value1=getValue(model1,field);
            Object value2=getValue(model2,field);
            if(value1==null||value2==null){
                continue;
            }
            JoinInfo joinInfo=getJoinInfo(model1.getClass(),field);
            if(joinInfo!=null){
                loadCriterion(prefix,value1,value2,condition);
                leftJoinList.add(joinInfo);
            }else {
                Criterion criterion=createCriterion(prefix,model1.getClass(),field,condition);
                criterion.setValue(value1);
                criterion.setSecondValue(value2);
                criterionList.add(criterion);
            }
        }
    }

    /**
     * 载入查询准则
     * @param model 属性值模板
     * @param order 排序方式
     */
    private void loadCriterion(Object model,Order order){
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
                loadCriterion(value,order);
                leftJoinList.add(joinInfo);
            }else {
                if(orderList==null){
                    orderList=new ArrayList<>();
                }
                Criterion criterion=createCriterion(null,model.getClass(),field,null);
                criterion.setCondition(order.getValue());
                orderList.add(criterion);
                break;
            }
        }
    }


//    /**
//     * 添加or条件，or两边的and条件自动加上括号
//     * @return
//     */
//    public Example<Model> or(){
//        List<Criterion> criterionList=new ArrayList<>();
//        orCriterionList.add(criterionList);
//        return this;
//    }

    /**
     * 添加右括号
     * @return
     */
    public Example<Model> rightParenthesis(){
        loadCriterion(null,Condition.RightParenthesis);
        return this;
    }

    /**
     * 添加左括号
     * @return
     */
    public Example<Model> orLeftParenthesis(){
        loadCriterion(Prefix.OR,Condition.LeftParenthesis);
        return this;
    }

    /**
     * 添加判断为null的条件，model内不为null的属性作为判空条件
     * @param model
     * @return
     */
    public Example<Model> orIsNull(Model model){
        loadCriterion(Prefix.OR,model, Condition.IsNull);
        return this;
    }

    /**
     * 添加判断不为null的条件，model内不为null的属性作为判断不为null条件
     * @param model
     * @return
     */
    public Example<Model> orIsNotNull(Model model){
        loadCriterion(Prefix.OR,model, Condition.IsNotNull);
        return this;
    }

    /**
     * 添加between条件，model内不为null的属性作为between条件
     * @param model1
     * @param model2
     * @return
     */
    public Example<Model> orBetween(Model model1,Model model2){
        loadCriterion(Prefix.OR,model1,model2, Condition.Between);
        return this;
    }

    /**
     * 添加等于条件，model内不为null的属性作为等于条件值
     * @param model
     * @return
     */
    public Example<Model> orEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.EqualTo);
        return this;
    }

    /**
     * 添加不等于条件，model内不为null的属性作为不等于条件的值
     * @param model
     * @return
     */
    public Example<Model> orNotEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.NotEqualTo);
        return this;
    }

    /**
     * 添加大于条件，model内不为null的属性作为大于条件的值
     * @param model
     * @return
     */
    public Example<Model> orGreaterThan(Model model){
        loadCriterion(Prefix.OR,model, Condition.GreaterThan);
        return this;
    }

    /**
     * 添加大于或等于条件，model内不为null的属性作为大于或等于条件的值
     * @param model
     * @return
     */
    public Example<Model> orGreaterThanOrEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.GreaterThanOrEqualTo);
        return this;
    }

    /**
     * 添加小于条件，model内不为null的属性作为小于条件
     * @param model
     * @return
     */
    public Example<Model> orLessThan(Model model){
        loadCriterion(Prefix.OR,model, Condition.LessThan);
        return this;
    }

    /**
     * 添加小于或等于条件，model内不为null的属性作为小于或等于条件
     * @param model
     * @return
     */
    public Example<Model> orLessThanOrEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.LessThanOrEqualTo);
        return this;
    }

    /**
     * 添加 like 条件，model内不为null的属性作为 like 条件
     * @param model
     * @return
     */
    public Example<Model> orLike(Model model){
        loadCriterion(Prefix.OR,model, Condition.Like);
        return this;
    }

    /**
     * 添加 not like 条件，model内不为 null 的属性作为 not like 条件的值
     * @param model
     * @return
     */
    public Example<Model> orNotLike(Model model){
        loadCriterion(Prefix.OR,model, Condition.NotLike);
        return this;
    }

    /**
     * 添加in条件，model内不为null的属性作为in条件
     * @param modelList
     * @return
     */
    public Example<Model> orIn(List<Model> modelList){
        loadCriterion(Prefix.OR,(List<Object>) modelList, Condition.In);
        return this;
    }

    /**
     * 添加左括号
     * @return
     */
    public Example<Model> andLeftParenthesis(){
        loadCriterion(Prefix.AND,Condition.LeftParenthesis);
        return this;
    }

    /**
     * 添加判断为null的条件，model内不为null的属性作为判空条件
     * @param model
     * @return
     */
    public Example<Model> andIsNull(Model model){
        loadCriterion(Prefix.AND,model, Condition.IsNull);
        return this;
    }

    /**
     * 添加判断不为null的条件，model内不为null的属性作为判断不为null条件
     * @param model
     * @return
     */
    public Example<Model> andIsNotNull(Model model){
        loadCriterion(Prefix.AND,model, Condition.IsNotNull);
        return this;
    }

    /**
     * 添加between条件，model内不为null的属性作为between条件
     * @param model1
     * @param model2
     * @return
     */
    public Example<Model> andBetween(Model model1,Model model2){
        loadCriterion(Prefix.AND,model1,model2, Condition.Between);
        return this;
    }

    /**
     * 添加等于条件，model内不为null的属性作为等于条件值
     * @param model
     * @return
     */
    public Example<Model> andEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.EqualTo);
        return this;
    }

    /**
     * 添加不等于条件，model内不为null的属性作为不等于条件的值
     * @param model
     * @return
     */
    public Example<Model> andNotEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.NotEqualTo);
        return this;
    }

    /**
     * 添加大于条件，model内不为null的属性作为大于条件的值
     * @param model
     * @return
     */
    public Example<Model> andGreaterThan(Model model){
        loadCriterion(Prefix.AND,model, Condition.GreaterThan);
        return this;
    }

    /**
     * 添加大于或等于条件，model内不为null的属性作为大于或等于条件的值
     * @param model
     * @return
     */
    public Example<Model> andGreaterThanOrEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.GreaterThanOrEqualTo);
        return this;
    }

    /**
     * 添加小于条件，model内不为null的属性作为小于条件
     * @param model
     * @return
     */
    public Example<Model> andLessThan(Model model){
        loadCriterion(Prefix.AND,model, Condition.LessThan);
        return this;
    }

    /**
     * 添加小于或等于条件，model内不为null的属性作为小于或等于条件
     * @param model
     * @return
     */
    public Example<Model> andLessThanOrEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.LessThanOrEqualTo);
        return this;
    }

    /**
     * 添加 like 条件，model内不为null的属性作为 like 条件
     * @param model
     * @return
     */
    public Example<Model> andLike(Model model){
        loadCriterion(Prefix.AND,model, Condition.Like);
        return this;
    }

    /**
     * 添加 not like 条件，model内不为 null 的属性作为 not like 条件的值
     * @param model
     * @return
     */
    public Example<Model> andNotLike(Model model){
        loadCriterion(Prefix.AND,model, Condition.NotLike);
        return this;
    }

    /**
     * 添加in条件，model内不为null的属性作为in条件
     * @param modelList
     * @return
     */
    public Example<Model> andIn(List<Model> modelList){
        loadCriterion(Prefix.AND,(List<Object>) modelList, Condition.In);
        return this;
    }

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

    /**
     * 根据model里不为null的属性排序，排序方式默认为ASC（正序）
     * @param model
     * @return
     */
    public Example<Model> orderBy(Model model){
        loadCriterion(model, Order.ASC);
        return this;
    }

    /**
     * 根据model里不为null的属性排序
     * @param model
     * @param order 排序方式，ASC（正序），DESC（倒序）
     * @return
     */
    public Example<Model> orderBy(Model model,Order order){
        loadCriterion(model,order);
        return this;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public List<Criterion> getCriterionList() {
        return criterionList;
    }

    public List<Criterion> getOrderList() {
        return orderList;
    }

    public Set<JoinInfo> getLeftJoinList() {
        return leftJoinList;
    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

}