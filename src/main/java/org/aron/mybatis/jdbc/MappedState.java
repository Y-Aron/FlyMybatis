package org.aron.mybatis.jdbc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aron.mybatis.annotation.provider.DeleteProvider;
import org.aron.mybatis.annotation.provider.InsertProvider;
import org.aron.mybatis.annotation.provider.SelectProvider;
import org.aron.mybatis.annotation.provider.UpdateProvider;
import org.aron.mybatis.annotation.sql.Delete;
import org.aron.mybatis.annotation.sql.Insert;
import org.aron.mybatis.annotation.sql.Select;
import org.aron.mybatis.annotation.sql.Update;
import org.aron.mybatis.jdbc.bean.ResultType;
import org.aron.mybatis.jdbc.bean.SQLType;
import org.aron.mybatis.jdbc.handler.ParameterHandler;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author: Y-Aron
 * @create: 2019-01-13 19:22
 **/
@Slf4j
public class MappedState {

    private Method method;
    private Object[] args;

    @Getter
    private String sql;

    @Getter
    private List<Object> parameters;

    @Getter
    private Class<?> clazz;

    @Getter
    private SQLType sqlType;

    @Getter
    private ResultType resultType;

    private List<String> placeholderArgs;

    private Map<String, Object> parsMap;

    private Method providerMethod;

    private Class<?> providerClass;

    private static final String AND = "AND";
    private static final String OR = "OR";

    public MappedState(Method method, Object[] args) {
        this.method = method;
        this.args = args;
        this.parameters = new ArrayList<>(0);
    }

    public void buildMapped(Object[] args) throws SQLException {
        if (providerMethod == null) {
            this.generateParsMap(args);
            generateSQL();
        } else {
            runProviderMethod(this.providerClass, this.providerMethod, args);
        }
    }

    /**
     * 执行SQL生成的代理方法
     */
    private void buildMappedProvider() throws SQLException {
        if (method.isAnnotationPresent(SelectProvider.class)) {
            sqlType = SQLType.SELECT;
            SelectProvider provider = method.getAnnotation(SelectProvider.class);
            baseProvider(provider.type(), provider.method());
        } else if (method.isAnnotationPresent(InsertProvider.class)) {
            sqlType = SQLType.INSERT;
            InsertProvider provider = method.getAnnotation(InsertProvider.class);
            baseProvider(provider.type(), provider.method());
        } else if (method.isAnnotationPresent(UpdateProvider.class)) {
            sqlType = SQLType.UPDATE;
            UpdateProvider provider = method.getAnnotation(UpdateProvider.class);
            baseProvider(provider.type(), provider.method());
        } else if (method.isAnnotationPresent(DeleteProvider.class)) {
            sqlType = SQLType.DELETE;
            DeleteProvider provider = method.getAnnotation(DeleteProvider.class);
            baseProvider(provider.type(), provider.method());
        } else {
            throw new SQLException("SQL cannot be empty");
        }
    }

    public void buildMapped() throws SQLException {
        if (method.isAnnotationPresent(Insert.class)) {
            this.generateParsMap(args);
            buildInsert(method.getAnnotation(Insert.class));
        } else if (method.isAnnotationPresent(Delete.class)) {
            this.generateParsMap(args);
            buildDelete(method.getAnnotation(Delete.class));
        } else if (method.isAnnotationPresent(Update.class)) {
            this.generateParsMap(args);
            buildUpdate(method.getAnnotation(Update.class));
        } else if (method.isAnnotationPresent(Select.class)) {
            this.generateParsMap(args);
            buildSelect(method.getAnnotation(Select.class));
        } else {
            buildMappedProvider();
        }
    }

    private void runProviderMethod(Class<?> providerClass, Method providerMethod, Object[] args) throws SQLException {
        try {
            this.sql = (String) providerMethod.invoke(providerClass.newInstance(), args);
            this.generateParsMap(args);
            baseBuild();
        } catch (IllegalAccessException | InstantiationException | SQLException | InvocationTargetException e) {
            throw new SQLException(providerClass + " -> method " + providerMethod + " execute exception");
        }
    }

    private void baseProvider(Class<?> providerClass, String methodName) throws SQLException {
        if (StringUtils.isBlank(methodName)) {
            throw new SQLException("methodName[" + methodName + "] is not blank");
        }
        try {
            this.providerClass = providerClass;
            this.providerMethod = providerClass.getMethod(methodName, this.method.getParameterTypes());
            runProviderMethod(this.providerClass, this.providerMethod, this.args);
        } catch (NoSuchMethodException e) {
            throw new SQLException(providerClass + " -> method " + methodName + " is not empty");
        }
    }

    private void buildSelect(Select select) throws SQLException {
        this.sql = select.value();
        if (!"select".equalsIgnoreCase(sql.split(" ")[0])) {
            throw new SQLException(sql + " is not select sql");
        }
        sqlType = SQLType.SELECT;
        baseBuild();
    }

    private void buildUpdate(Update update) throws SQLException {
        sqlType = SQLType.UPDATE;
        this.sql = update.value();
        if (StringUtils.isBlank(this.sql)) {
            buildUpdatePlus(update);
        } else if (!"update".equalsIgnoreCase(sql.split(" ")[0])) {
            throw new SQLException(sql + " is not update sql");
        }
        baseBuild();
    }

    private void buildUpdatePlus(Update update) throws SQLException {
        String[] fields = update.fields();
        checkTableAndFields(update.table(), fields);
        // UPDATE tb_user
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(update.table());

        sb.append(" SET");
        List<String> flag = new ArrayList<>(0);
        List<String> values = new ArrayList<>(0);
        parsMap.keySet().forEach(key -> {
            String name = format(key);
            if (flag.contains(name)) {
                return;
            }
            if (ArrayUtils.contains(fields, name)) {
                flag.add(name);
                values.add(" `"+ name +"`=#{"+ key +"}");
            }
        });
        if (flag.size() < fields.length) {
            throw new SQLException("SQL creation failed. Please check the grammar ");
        }
        sb.append(StringUtils.join(values, ","));
        sb.append(generateCondition(update.and(), update.or()));
        this.sql = sb.toString();
    }

    private void buildDeletePlus(Delete delete) throws SQLException {
        checkTable(delete.table());
        this.sql = "DELETE FROM " + delete.table() +
                generateCondition(delete.and(), delete.or());
        log.debug("sql: {}", sql);
    }

    private void buildInsertPlus(Insert insert) throws SQLException {
        String[] fields = insert.fields();
        checkTableAndFields(insert.table(), fields);
        List<String> values = new ArrayList<>(0);
        List<String> nameList = new ArrayList<>(0);
        parsMap.keySet().forEach(key -> {
            String name = format(key);
            if (nameList.contains("`" + name + "`")) {
                return;
            }
            if (ArrayUtils.contains(fields, name)) {
                values.add("#{"+ key +"}");
                nameList.add("`"+ name +"`");
            }
        });
        this.sql = "INSERT INTO " + insert.table() +
                '(' + StringUtils.join(nameList, ", ") + ')' +
                " VALUES(" + StringUtils.join(values, ", ") + ')';
    }

    private void buildDelete(Delete delete) throws SQLException {
        sqlType = SQLType.DELETE;
        this.sql = delete.value();
        if (StringUtils.isBlank(this.sql)) {
            buildDeletePlus(delete);
        } else if (!"delete".equalsIgnoreCase(sql.split(" ")[0])) {
            throw new SQLException(sql + " is not delete sql");
        }
        baseBuild();
    }

    private void buildInsert(Insert insert) throws SQLException {
        sqlType = SQLType.INSERT;
        this.sql = insert.value();
        if (StringUtils.isBlank(this.sql)) {
           buildInsertPlus(insert);
        } else if (!"insert".equalsIgnoreCase(sql.split(" ")[0])) {
            throw new SQLException(sql + " is not insert sql");
        }
        baseBuild();
    }

    private void generateParsMap(Object[] args) {
        if (providerMethod != null) {
            parsMap = ParameterHandler.getMethodParsMap(providerMethod, args);
        } else {
            parsMap = ParameterHandler.getMethodParsMap(method, args);
        }
    }

    private String generateCondition(String[] ands, String[] ors) throws SQLException {
        StringBuilder sb = new StringBuilder();
        String and = joinCondition(ands, AND, true);
        sb.append(and);
        sb.append(joinCondition(ors, OR, "".equals(and)));
        if (StringUtils.isNotBlank(sb)) {
            return " WHERE" + sb.toString();
        }
        return sb.toString();
    }

    private String joinCondition(String[] fields, String separator, boolean first) throws SQLException {
        if (ArrayUtils.isEmpty(fields)) {
            return "";
        }
        List<String> flag = new ArrayList<>(0);
        List<String> values = new ArrayList<>(0);
        parsMap.forEach((key, value) -> {
            if (flag.contains(key)) {
                return;
            }
            if (ArrayUtils.contains(fields, key)) {
                flag.add(key);
                if (value.getClass().isArray()) {
                    values.add(" `"+ key +"` IN #{"+ key +"} ");
                } else {
                    values.add(" `"+ key +"`=#{"+ key +"} ");
                }
            }
        });
        if (flag.size() < fields.length) {
            throw new SQLException("SQL creation failed. Please check the grammar ");
        }
        if (!first) {
            return separator + StringUtils.join(values, separator);
        }
        return StringUtils.join(values, separator);
    }

    /**
     * 校验表名是否为空
     */
    private void checkTable(String table) throws SQLException {
        if (StringUtils.isBlank(table)) {
            throw new SQLException("tableName[" + table + "] is not blank");
        }
    }

    /**
     * 校验表名和字段是否为空
     * @param table 表名
     * @param fields 字段
     */
    private void checkTableAndFields(String table, String[] fields) throws SQLException {
        checkTable(table);
        if (ArrayUtils.isEmpty(fields)) {
            throw new SQLException("the array of field names for the table[" + table + "] cannot be empty");
        }
    }

    /**
     * 构建mapped
     * 1. 获取sql带有`#{}`的参数列表
     * 2. 解析sql, 将占位符替换成 `?` 形式
     * 3. 解析方法结束对象属于哪一类型
     */
    private void baseBuild() throws SQLException {
        // 1. 获取sql带有`#{}`的参数列表
        placeholderArgs = ParameterHandler.getSqlParameters(sql);
        // 2. 解析sql, 将占位符替换成 `?` 形式
        generateSQL();
        // 3. 解析方法结束对象属于哪一类型
        if (resultType == null) {
            parseResult(method);
        }
    }

    /**
     * 如果sql中存在占位符 `#{}`则将其解析成 `?` 形式
     * 如果占位符参数的类型为数组类型, 则将其解析成 `(?, ?, ...)` 形式
     */
    private void generateSQL() throws SQLException {
        if (CollectionUtils.isEmpty(placeholderArgs)) {
            return;
        }
        parameters = new ArrayList<>(placeholderArgs.size());
        if (MapUtils.isNotEmpty(parsMap)) {
            for (String key : placeholderArgs) {
                String name = key.replaceAll("[#{}]", "");
                Object value = parsMap.get(name);
                if (!parsMap.containsKey(name) || value == null) {
                    throw new SQLException("parameter[" + name + "] cannot be null");
                } else {
                    if (parsMap.get(name).getClass().isArray()) {
                        sql = StringUtils.replace(sql, key, parseArray(value, name));
                    } else {
                        parameters.add(value);
                        sql = StringUtils.replace(sql, key, "?");
                    }
                }
            }
        }
    }


    /**
     * 1. 自定义类 -> ONE_CLASS
     * 2. 判断是否是list集合 -> LIST_CLASS
     *      - 判断存储对象是否是自定义类   -> LIST_CUSTOM_CLASS
     *      - 判断存储对象是否是map类     -> LIST_MAP_CLASS
     * 3. 判断是否是map集合
     *   - 判断map集合的value是否是自定义类  -> MAP_CUSTOM_CLASS
     *   - 判断map集合的value是否是非自定义类 -> MAP_NOT_CUSTOM_CLASS
     *  - 非集合类型 -> ONE_CLASS
     */
    private void parseResult(Method method) {
        this.clazz = method.getReturnType();
        this.resultType = ResultType.ONE_CLASS;

        if (this.clazz.getClassLoader() != null) {
            return;
        }
        if (this.clazz.equals(List.class)) {
            ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
            Type tmpType = type.getActualTypeArguments()[0];
            try {
                this.clazz = (Class<?>) tmpType;
                this.resultType = ResultType.LIST_ONE_CLASS;
            } catch (Exception e) {
                ParameterizedType mapType = (ParameterizedType) tmpType;
                if (mapType.getRawType().equals(Map.class)) {
                    this.resultType = ResultType.LIST_MAP_CLASS;
                }
            }
        }
        if (clazz.equals(Map.class)) {
            ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
            this.clazz = (Class<?>) type.getActualTypeArguments()[1];
            if (this.clazz.getClassLoader() != null) {
                this.resultType = ResultType.MAP_CUSTOM_CLASS;
            } else {
                this.resultType = ResultType.MAP_NOT_CUSTOM_CLASS;
            }
        }
    }

    /**
     * 当object为数组时，将数组解析成 (?, ? ...) 形式
     * @param object 参数对象
     * @param name sql 占位符名称
     */
    private String parseArray(Object object, String name) throws SQLException {
        int length = Array.getLength(object);
        if (length == 0) {
            throw new SQLException("array[" + name + "] cannot be empty");
        }
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int index = 0; index < length; index++) {
            if (index == length - 1) {
                sb.append('?');
            } else {
                sb.append('?').append(',');
            }
            parameters.add(Array.get(object, index));
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * 格式化占位符
     * 1. test.name -> name
     * 2. name -> name
     */
    private String format(String old) {
        int pos;
        String name = old;
        if ((pos = old.indexOf(".")) > 0) {
            name = old.substring(pos + 1);
        }
        return name;
    }
}

