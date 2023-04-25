package com.githut.laich.tools.fm.enums;

/**
 * eq 就是 equal等于
 * ne就是 not equal不等于
 * gt 就是 greater than大于
 * lt 就是 less than小于
 * ge 就是 greater than or equal 大于等于
 * le 就是 less than or equal 小于等于
 * in 就是 in 包含（数组）
 * isNull 就是 等于null
 * between 就是 在2个条件之间(包括边界值)
 * like 就是 模糊查询
 * @author handson
 */
public enum QueryEnum {
    eq,
    like,
    ge,
    le,
    gt,
    lt,
    in;
}
