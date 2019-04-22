package org.aron.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 18:16
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapper {
    String value() default "";
}
