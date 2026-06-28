package io.github.lhcyh.lhmybatis.assistant.example;

import io.github.lhcyh.lhmybatis.*;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ConditionClause<Model> {
    private List<Criterion> criterionList;
    private Set<JoinInfo> leftJoinList;
    public ConditionClause(List<Criterion> criterionList, Set<JoinInfo> joinInfoSet){
        this.criterionList=criterionList;
        this.leftJoinList=joinInfoSet;
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
        List<String> possibleMethodNames = new ArrayList<>();

        // 添加基于 "get" 前缀的可能名称 (适用于所有类型)
        possibleMethodNames.add("get" + capitalize(fieldName));
        possibleMethodNames.add("get" + fieldName); // 处理 tSum -> gettSum 等情况

        // 如果是布尔类型，添加基于 "is" 前缀的可能名称
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            possibleMethodNames.add("is" + capitalize(fieldName));
            possibleMethodNames.add("is" + fieldName); // 处理 tSum -> istSum 等情况
        }

        // 循环尝试列表中的每个方法名
        for (String methodName : possibleMethodNames) {
            try {
                return model.getClass().getMethod(methodName);
            } catch (NoSuchMethodException e) {
                // 捕获异常并继续尝试下一个名称，无需做任何操作
                // 可以在此处添加调试日志 if (debugEnabled) log.debug(...)
            }
        }

        // 所有可能的名称都尝试过，但没有找到
        System.out.println("Getter method not found for field: " + fieldName +
                ". Tried methods: " + possibleMethodNames);
        return null; // 或者根据需要抛出异常
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
    protected JoinInfo getJoinInfo(Class tClass,Field field){
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
     * @param model 属性值模版
     * @return 属性列表
     */
    protected List<Field> getFieldList(Object model){
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
     * @param model 属性值模版
     * @param field 属性
     * @return 属性值
     */
    protected Object getValue(Object model,Field field){
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

//    private String handlePrefix(Prefix prefix){
//        if(prefix==null){
//            return null;
//        }
//        if(criterionList.size()>0){
//            Criterion criterion=criterionList.get(criterionList.size()-1);
//            Condition condition=Condition.getConditionByValue(criterion.getCondition());
//            if(condition==Condition.LeftParenthesis){
//                return null;
//            }else {
//                return prefix.name();
//            }
//        }
//        return prefix.name();
//    }

    protected String getAttribute(Class tClass,Field field){
        String tableName=getTableName(tClass);
        String fieldName=getFiledName(field);
        StringBuilder attribute=new StringBuilder();
        attribute.append("`");
        attribute.append(tableName);
        attribute.append("`.");
        attribute.append(fieldName);
        return attribute.toString();
    }

    protected String handlePrefix(Prefix prefix){
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
        }else{
            return null;
        }
    }

    /**
     * 创建查询准则
     * @param prefix 前缀（and或or）
     * @param tClass 模版类
     * @param field 属性列
     * @param condition 条件
     * @return 查询准则
     */
    protected Criterion createCriterion(Prefix prefix, Class tClass, Field field, Condition condition){
        Criterion criterion=new Criterion();
//        String tableName=getTableName(tClass);
//        criterion.setTable(tableName);
//        String fieldName=getFiledName(field);
//        criterion.setField(fieldName);
        String attribute=getAttribute(tClass,field);
        criterion.setAttribute(attribute);
        criterion.setPrefix(handlePrefix(prefix));
        if(condition!=null){
            criterion.setCondition(condition.getValue());
            criterion.setValueType(condition.getValueType().getValue());
        }
        return criterion;
    }

//    /**
//     * 载入查询准则
//     * @param condition 判断条件（无表属性无值条件）
//     */
//    private void loadCriterion(Prefix prefix,Condition condition){
//        if(condition.getValueType()!= ValueType.NoValue){
//            new Exception("Parameter error").printStackTrace();
//            return;
//        }
//        Criterion criterion=new Criterion();
//        criterion.setCondition(condition.getValue());
//        criterion.setValueType(condition.getValueType().getValue());
//        criterion.setPrefix(handlePrefix(prefix));
//        criterionList.add(criterion);
//    }

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
     * 添加判断为null的条件，model内不为null的属性作为判空条件
     * @param model
     */
    public void orIsNull(Model model){
        loadCriterion(Prefix.OR,model, Condition.IsNull);
    }

    /**
     * 添加判断不为null的条件，model内不为null的属性作为判断不为null条件
     * @param model
     */
    public void orIsNotNull(Model model){
        loadCriterion(Prefix.OR,model, Condition.IsNotNull);
    }

    /**
     * 添加between条件，model内不为null的属性作为between条件
     * @param model1
     * @param model2
     */
    public void orBetween(Model model1,Model model2){
        loadCriterion(Prefix.OR,model1,model2, Condition.Between);
    }

    /**
     * 添加等于条件，model内不为null的属性作为等于条件值
     * @param model
     */
    public void orEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.EqualTo);
    }

    /**
     * 添加不等于条件，model内不为null的属性作为不等于条件的值
     * @param model
     */
    public void orNotEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.NotEqualTo);
    }

    /**
     * 添加大于条件，model内不为null的属性作为大于条件的值
     * @param model
     */
    public void orGreaterThan(Model model){
        loadCriterion(Prefix.OR,model, Condition.GreaterThan);
    }

    /**
     * 添加大于或等于条件，model内不为null的属性作为大于或等于条件的值
     * @param model
     */
    public void orGreaterThanOrEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.GreaterThanOrEqualTo);
    }

    /**
     * 添加小于条件，model内不为null的属性作为小于条件
     * @param model
     */
    public void orLessThan(Model model){
        loadCriterion(Prefix.OR,model, Condition.LessThan);
    }

    /**
     * 添加小于或等于条件，model内不为null的属性作为小于或等于条件
     * @param model
     */
    public void orLessThanOrEqualTo(Model model){
        loadCriterion(Prefix.OR,model, Condition.LessThanOrEqualTo);
    }

    /**
     * 添加 like 条件，model内不为null的属性作为 like 条件
     * @param model
     */
    public void orLike(Model model){
        loadCriterion(Prefix.OR,model, Condition.Like);
    }

    /**
     * 添加 not like 条件，model内不为 null 的属性作为 not like 条件的值
     * @param model
     */
    public void orNotLike(Model model){
        loadCriterion(Prefix.OR,model, Condition.NotLike);
    }

    /**
     * 添加in条件，model内不为null的属性作为in条件
     * @param modelList
     */
    public void orIn(List<Model> modelList){
        loadCriterion(Prefix.OR,(List<Object>) modelList, Condition.In);
    }

    /**
     * 添加判断为null的条件，model内不为null的属性作为判空条件
     * @param model
     */
    public void andIsNull(Model model){
        loadCriterion(Prefix.AND,model, Condition.IsNull);
    }

    /**
     * 添加判断不为null的条件，model内不为null的属性作为判断不为null条件
     * @param model
     */
    public void andIsNotNull(Model model){
        loadCriterion(Prefix.AND,model, Condition.IsNotNull);
    }

    /**
     * 添加between条件，model内不为null的属性作为between条件
     * @param model1
     * @param model2
     */
    public void andBetween(Model model1,Model model2){
        loadCriterion(Prefix.AND,model1,model2, Condition.Between);
    }

    /**
     * 添加等于条件，model内不为null的属性作为等于条件值
     * @param model
     */
    public void andEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.EqualTo);
    }

    /**
     * 添加不等于条件，model内不为null的属性作为不等于条件的值
     * @param model
     */
    public void andNotEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.NotEqualTo);
    }

    /**
     * 添加大于条件，model内不为null的属性作为大于条件的值
     * @param model
     */
    public void andGreaterThan(Model model){
        loadCriterion(Prefix.AND,model, Condition.GreaterThan);
    }

    /**
     * 添加大于或等于条件，model内不为null的属性作为大于或等于条件的值
     * @param model
     */
    public void andGreaterThanOrEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.GreaterThanOrEqualTo);
    }

    /**
     * 添加小于条件，model内不为null的属性作为小于条件
     * @param model
     */
    public void andLessThan(Model model){
        loadCriterion(Prefix.AND,model, Condition.LessThan);
    }

    /**
     * 添加小于或等于条件，model内不为null的属性作为小于或等于条件
     * @param model
     */
    public void andLessThanOrEqualTo(Model model){
        loadCriterion(Prefix.AND,model, Condition.LessThanOrEqualTo);
    }

    /**
     * 添加 like 条件，model内不为null的属性作为 like 条件
     * @param model
     */
    public void andLike(Model model){
        loadCriterion(Prefix.AND,model, Condition.Like);
    }

    /**
     * 添加 not like 条件，model内不为 null 的属性作为 not like 条件的值
     * @param model
     */
    public void andNotLike(Model model){
        loadCriterion(Prefix.AND,model, Condition.NotLike);
    }

    /**
     * 添加in条件，model内不为null的属性作为in条件
     * @param modelList
     */
    public void andIn(List<Model> modelList){
        loadCriterion(Prefix.AND,(List<Object>) modelList, Condition.In);
    }

    public List<Criterion> getCriterionList() {
        return criterionList;
    }

    public Set<JoinInfo> getJoinInfoList() {
        return leftJoinList;
    }
}
