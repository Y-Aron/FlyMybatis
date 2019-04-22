package org.aron.mybatis.jdbc.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aron.mybatis.annotation.Param;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 00:53
 **/
@Slf4j
public class ParameterHandler {

    public static Class<?> getResultType(Method method) {
        return  null;
    }

    public static List<String> getSqlParameters(String sql) {
        String reg = "#\\{[\\w.]+}";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(sql);
        List<String> params = new ArrayList<>(1);
        while (matcher.find()) {
            params.add(matcher.group());
        }
        return params;
    }

    public static Map<String, Object> getMethodParsMap(Method method, Object... args) {
        Map<String, Object> parMap = new HashMap<>(0);
        if (ArrayUtils.isEmpty(args) || method == null) {
            return parMap;
        }
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            if (parameter.isAnnotationPresent(Param.class)) {
                Param param = parameter.getAnnotation(Param.class);
                if (!"".equals(param.value())) {
                    name = param.value();
                }
            }
            //  判断是否是自定义类
            if (parameter.getType().getClassLoader() != null) {
                Field[] fields = parameter.getType().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        parMap.put(name + "." + field.getName(), field.get(args[i]));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                parMap.put(name, args[i]);
            }
        }
        return parMap;
    }
}
