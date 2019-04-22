package org.aron.mybatis.annotation.sql;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 18:06
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Select {
    String value();
}
