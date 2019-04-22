package org.aron.mybatis.jdbc.handler;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 01:02
 **/
@Slf4j
public class ResultSetHandler {

    public static Map<String, Object> handlerOne(ResultSet resultSet) throws SQLException {
        List<String> columnNames = getColumnNames(resultSet);
        Map<String, Object> map = new HashMap<>();
        if (resultSet.next()) {
            for (String name : columnNames) {
                map.put(name, resultSet.getObject(name));
            }
        }
        return map;
    }

    public static Object handlerOne(ResultSet resultSet, Class<?> clazz) throws SQLException, InstantiationException, IllegalAccessException {
        List<String> columnNames = getColumnNames(resultSet);
        if (resultSet.next()) {
            return baseHandler(resultSet, clazz, columnNames);
        }
        return null;
    }

    public static List<Object> handler(ResultSet resultSet, Class<?> clazz) throws SQLException, InstantiationException, IllegalAccessException {
        List<String> columnNames = getColumnNames(resultSet);
        List<Object> list = new ArrayList<>();
        while (resultSet.next()) {
            Object object = baseHandler(resultSet, clazz, columnNames);
            if (object == null) {
                break;
            } else {
                list.add(object);
            }
        }
        return list;
    }

    public static <K, V> List<Map<K, V>> handlerMap(ResultSet resultSet) throws SQLException {
        List<String> columnNames = getColumnNames(resultSet);
        List<Map<K, V>> list = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> map = baseHandlerMap(resultSet, columnNames);
            //noinspection unchecked
            list.add((Map<K, V>) map);
        }
        return list;
    }

    private static Map<String, Object> baseHandlerMap(ResultSet resultset, List<String> columnNames) throws SQLException {
        Map<String, Object> map = new HashMap<>(columnNames.size());
        for (String name : columnNames) {
            map.put(name, resultset.getObject(name));
        }
        return map;
    }

    private static List<String> getColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> list = new ArrayList<>(count);
        for (int i = 1; i<= count; i++) {
            list.add(metaData.getColumnLabel(i).toLowerCase());
        }
        return list;
    }

    private static Object baseHandler(ResultSet resultSet, Class<?> clazz, List<String> columnNames) throws SQLException, IllegalAccessException, InstantiationException {
        Object object;
        if (clazz.getClassLoader() != null) {
            Field[] fields = clazz.getDeclaredFields();
            object = clazz.newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName().replaceAll("_", "");
                if (columnNames.contains(name.toLowerCase())) {
                    field.set(object, resultSet.getObject(name));
                }
            }
        } else {
            object = resultSet.getObject(1);
        }
        return object;
    }
}
