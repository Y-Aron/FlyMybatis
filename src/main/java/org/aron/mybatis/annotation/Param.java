package org.aron.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 18:06
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    String value();
}
