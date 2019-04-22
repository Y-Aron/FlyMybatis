package org.aron.mybatis.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aron.mybatis.jdbc.Environment;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 18:09
 **/
@Slf4j
public class Configuration {
    private Map<String, Environment> environmentMap = new ConcurrentHashMap<>(0);

    private Map<Method, String> methodEnvironmentMap = new ConcurrentHashMap<>(0);

    private boolean autoCommit = true;

    private Map<String, String> propertyMap;

    @Getter
    private String primary = "default";

    @Getter
    @Setter
    private String proxyMode = "jdbc";

    public Configuration() {}

    public Configuration(Environment... environments) {
        log.trace("----------初始化环境信息----------");
        for (Environment environment : environments) {
//            environment.getTransaction().setConfiguration(this);
            environmentMap.put(environment.getName(), environment);
        }
        log.trace("----------初始化环境信息完毕！----------");
    }

    public String getEnvironment(Method method) {
        if (method == null) {
            return null;
        }
        return methodEnvironmentMap.get(method);
    }

    /**
     * 初始化配置参数
     * @param map 配置参数Map
     */
    public void initialize(Map<String, String> map) {
        // 配置参数Map
        if (this.propertyMap == null) {
            this.propertyMap = map;
        }
        // 设置代理模式
        this.proxyMode = propertyMap.getOrDefault("mybatis.proxy.mode", proxyMode);

        // 设置默认数据源
        this.primary = propertyMap.getOrDefault("datasource.primary", primary);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();

            if (!StringUtils.containsIgnoreCase(key, "datasource")) {
                continue;
            }

            String name = StringUtils.substringBefore(key, ".");
            if ("datasource".equalsIgnoreCase(name)) {
                name = this.primary;
            }
            if (!environmentMap.containsKey(name)) {
                environmentMap.put(name, new Environment(name));

            }
            String jdbcKey = StringUtils.substringAfter(key, "datasource.");
            environmentMap.get(name).setJdbcProperty(jdbcKey, entry.getValue());
        }
        environmentMap.forEach((name, environment) -> {
            log.debug("name: {}, environment: {}", name, environment);
            try {
                environment.initialize();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Environment getEnvironment(String name) {
        if (name == null) {
            return null;
        }
        return environmentMap.get(name);
    }

    public void registerEnvironment(Method method, String environment) {
        if (!methodEnvironmentMap.containsKey(method)) {
            methodEnvironmentMap.put(method, environment);
        }
    }
}
