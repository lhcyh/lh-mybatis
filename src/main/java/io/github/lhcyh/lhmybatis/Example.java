package io.github.lhcyh.lhmybatis;

import io.github.lhcyh.lhmybatis.assistant.example.Condition;
import io.github.lhcyh.lhmybatis.assistant.example.Criterion;
import io.github.lhcyh.lhmybatis.assistant.example.JoinInfo;
import io.github.lhcyh.lhmybatis.assistant.example.ValueType;
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
    /** or准则数组，数组每一项是and准则数组 **/
    private List<List<Criterion>> orCriterionList;
    /** 要连接的表名数组 **/
    private Set<JoinInfo> leftJoinList;
    /** sql语句的limit的起始行数，从0开始 **/
    private Integer limitStart;
    /** 要查询的条数 **/
    private Integer limitNum;
    /** 排序准则 **/
    private Criterion orderBy;

    public Example(){
        orCriterionList=new ArrayList<>();
        List<Criterion> criterionList=new ArrayList<>();
        orCriterionList.add(criterionList);
        leftJoinList=new HashSet<>();
    }

    /**
     * 获取get方法
     * @param model
     * @param field
     * @return
     */
    private Method getGetMethod(Object model,Field field){
        // 获取属性的名字
        String property = field.getName();
        // 将属性的首字符大写，方便构造get，set方法
        String name = property.substring(0, 1).toUpperCase() + property.substring(1);
        Method m=null;

        try {
            m = model.getClass().getMethod("get" + name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return m;
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

    /**
     * 创建查询准则
     * @param tClass
     * @param field
     * @param condition
     * @return
     */
    private Criterion createCriterion(Class tClass, Field field, Condition condition){
        Criterion criterion=new Criterion();
        String tableName=getTableName(tClass);
        criterion.setTable(tableName);
        String fieldName=getFiledName(field);
        criterion.setField(fieldName);
        if(condition!=null){
            criterion.setCondition(condition.getValue());
            criterion.setValueType(condition.getValueType().getValue());
        }
        return criterion;
    }

    /**
     * 载入查询准则
     * @param model 属性值模板
     * @param condition 判断条件（单值条件或无值条件）
     */
    private void loadCriterion(Object model, Condition condition){
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
                loadCriterion(value,condition);
            }else {
                Criterion criterion=createCriterion(model.getClass(),field,condition);
                criterion.setValue(value);
                orCriterionList.get(orCriterionList.size() - 1).add(criterion);
            }
        }
    }

    /**
     * 载入查询准则
     * @param modelList 属性模板列表
     * @param condition 判断条件（多值条件）
     */
    private void loadCriterion(List<Object> modelList,Condition condition){
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
                    loadCriterion(valueList,condition);
                    leftJoinList.add(joinInfo);
                }else {
                    Criterion criterion=createCriterion(modelList.get(0).getClass(),field,condition);
                    criterion.setValue(valueList);
                    orCriterionList.get(orCriterionList.size()-1).add(criterion);
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
    private void loadCriterion(Object model1,Object model2,Condition condition){
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
                loadCriterion(value1,value2,condition);
                leftJoinList.add(joinInfo);
            }else {
                Criterion criterion=createCriterion(model1.getClass(),field,condition);
                criterion.setValue(value1);
                criterion.setSecondValue(value2);
                orCriterionList.get(orCriterionList.size()-1).add(criterion);
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
                Criterion criterion=createCriterion(model.getClass(),field,null);
                criterion.setCondition(order.getValue());
                orderBy=criterion;
                break;
            }
        }
    }


    /**
     * 添加or条件，or两边的and条件自动加上括号
     * @return
     */
    public Example<Model> or(){
        List<Criterion> criterionList=new ArrayList<>();
        orCriterionList.add(criterionList);
        return this;
    }

    /**
     * 添加判断为null的条件，model内不为null的属性作为判空条件
     * @param model
     * @return
     */
    public Example<Model> andIsNull(Model model){
        loadCriterion(model, Condition.IsNull);
        return this;
    }

    /**
     * 添加判断不为null的条件，model内不为null的属性作为判断不为null条件
     * @param model
     * @return
     */
    public Example<Model> andIsNotNull(Model model){
        loadCriterion(model, Condition.IsNotNull);
        return this;
    }

    /**
     * 添加between条件，model内不为null的属性作为between条件
     * @param model1
     * @param model2
     * @return
     */
    public Example<Model> andBetween(Model model1,Model model2){
        loadCriterion(model1,model2, Condition.Between);
        return this;
    }

    /**
     * 添加等于条件，model内不为null的属性作为等于条件值
     * @param model
     * @return
     */
    public Example<Model> andEqualTo(Model model){
        loadCriterion(model, Condition.EqualTo);
        return this;
    }

    /**
     * 添加不等于条件，model内不为null的属性作为不等于条件的值
     * @param model
     * @return
     */
    public Example<Model> andNotEqualTo(Model model){
        loadCriterion(model, Condition.NotEqualTo);
        return this;
    }

    /**
     * 添加大于条件，model内不为null的属性作为大于条件的值
     * @param model
     * @return
     */
    public Example<Model> andGreaterThan(Model model){
        loadCriterion(model, Condition.GreaterThan);
        return this;
    }

    /**
     * 添加小于条件，model内不为null的属性作为小于条件
     * @param model
     * @return
     */
    public Example<Model> andLessThan(Model model){
        loadCriterion(model, Condition.LessThan);
        return this;
    }

    /**
     * 添加小于或等于条件，model内不为null的属性作为小于或等于条件
     * @param model
     * @return
     */
    public Example<Model> andLessThanOrEqualTo(Model model){
        loadCriterion(model, Condition.LessThanOrEqualTo);
        return this;
    }

    /**
     * 添加 like 条件，model内不为null的属性作为 like 条件
     * @param model
     * @return
     */
    public Example<Model> andLike(Model model){
        loadCriterion(model, Condition.Like);
        return this;
    }

    /**
     * 添加 not like 条件，model内不为 null 的属性作为 not like 条件的值
     * @param model
     * @return
     */
    public Example<Model> andNotLike(Model model){
        loadCriterion(model, Condition.NotLike);
        return this;
    }

    /**
     * 添加in条件，model内不为null的属性作为in条件
     * @param modelList
     * @return
     */
    public Example<Model> andIn(List<Model> modelList){
        loadCriterion((List<Object>) modelList, Condition.In);
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

    public List<List<Criterion>> getOrCriterionList() {
        return orCriterionList;
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

    public Criterion getOrderBy() {
        return orderBy;
    }

}