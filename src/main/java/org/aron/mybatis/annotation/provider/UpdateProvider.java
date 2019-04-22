package org.aron.mybatis.annotation.provider;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-20 14:19
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UpdateProvider {
    Class<?> type();
    String method();
}
