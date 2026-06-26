package io.github.lhcyh.lhmybatis.assistant.example;

import io.github.lhcyh.lhmybatis.Example;

import java.util.List;

public interface Having<Model> {
    /**
     * 添加右括号
     * @return
     */
    public Having<Model> rightParenthesis();

    /**
     * 添加左括号
     * @return
     */
    public Example<Model> orLeftParenthesis();

    /**
     * 添加判断为null的条件，model内不为null的属性作为判空条件
     * @param model
     * @return
     */
    public Example<Model> orIsNull(Aggregate aggregate,Model model);

    /**
     * 添加判断不为null的条件，model内不为null的属性作为判断不为null条件
     * @param model
     * @return
     */
    public Example<Model> orIsNotNull(Aggregate aggregate,Model model);

    /**
     * 添加between条件，model内不为null的属性作为between条件
     * @param model1
     * @param model2
     * @return
     */
    public Example<Model> orBetween(Aggregate aggregate,Model model1,Model model2);

    /**
     * 添加等于条件，model内不为null的属性作为等于条件值
     * @param model
     * @return
     */
    public Example<Model> orEqualTo(Model model);

    /**
     * 添加不等于条件，model内不为null的属性作为不等于条件的值
     * @param model
     * @return
     */
    public Example<Model> orNotEqualTo(Model model);

    /**
     * 添加大于条件，model内不为null的属性作为大于条件的值
     * @param model
     * @return
     */
    public Example<Model> orGreaterThan(Model model);

    /**
     * 添加大于或等于条件，model内不为null的属性作为大于或等于条件的值
     * @param model
     * @return
     */
    public Example<Model> orGreaterThanOrEqualTo(Model model);

    /**
     * 添加小于条件，model内不为null的属性作为小于条件
     * @param model
     * @return
     */
    public Example<Model> orLessThan(Model model);

    /**
     * 添加小于或等于条件，model内不为null的属性作为小于或等于条件
     * @param model
     * @return
     */
    public Example<Model> orLessThanOrEqualTo(Model model);

    /**
     * 添加 like 条件，model内不为null的属性作为 like 条件
     * @param model
     * @return
     */
    public Example<Model> orLike(Model model);

    /**
     * 添加 not like 条件，model内不为 null 的属性作为 not like 条件的值
     * @param model
     * @return
     */
    public Example<Model> orNotLike(Model model);

    /**
     * 添加in条件，model内不为null的属性作为in条件
     * @param modelList
     * @return
     */
    public Example<Model> orIn(List<Model> modelList);

    /**
     * 添加左括号
     * @return
     */
    public Example<Model> andLeftParenthesis();

    /**
     * 添加判断为null的条件，model内不为null的属性作为判空条件
     * @param model
     * @return
     */
    public Example<Model> andIsNull(Model model);

    /**
     * 添加判断不为null的条件，model内不为null的属性作为判断不为null条件
     * @param model
     * @return
     */
    public Example<Model> andIsNotNull(Model model);

    /**
     * 添加between条件，model内不为null的属性作为between条件
     * @param model1
     * @param model2
     * @return
     */
    public Example<Model> andBetween(Model model1,Model model2);

    /**
     * 添加等于条件，model内不为null的属性作为等于条件值
     * @param model
     * @return
     */
    public Example<Model> andEqualTo(Model model);

    /**
     * 添加不等于条件，model内不为null的属性作为不等于条件的值
     * @param model
     * @return
     */
    public Example<Model> andNotEqualTo(Model model);

    /**
     * 添加大于条件，model内不为null的属性作为大于条件的值
     * @param model
     * @return
     */
    public Example<Model> andGreaterThan(Model model);

    /**
     * 添加大于或等于条件，model内不为null的属性作为大于或等于条件的值
     * @param model
     * @return
     */
    public Example<Model> andGreaterThanOrEqualTo(Model model);

    /**
     * 添加小于条件，model内不为null的属性作为小于条件
     * @param model
     * @return
     */
    public Example<Model> andLessThan(Model model);

    /**
     * 添加小于或等于条件，model内不为null的属性作为小于或等于条件
     * @param model
     * @return
     */
    public Example<Model> andLessThanOrEqualTo(Model model);


    /**
     * 添加in条件，model内不为null的属性作为in条件
     * @param modelList
     * @return
     */
    public Example<Model> andIn(List<Model> modelList);
}
