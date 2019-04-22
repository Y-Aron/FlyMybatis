package org.aron.mybatis.jdbc.bean;

/**
 * @author: Y-Aron
 * @create: 2019-01-18 10:57
 **/
public enum  ResultType {
    // 单个类
    ONE_CLASS,

    // List<单个类>
    LIST_ONE_CLASS,

    // List<Map<K, V>>
    LIST_MAP_CLASS,

    // Map<String, 自定义类>
    MAP_CUSTOM_CLASS,

    // Map<String, 非自定义类>
    MAP_NOT_CUSTOM_CLASS
}
