package org.aron.mybatis.core.session;

import org.aron.commons.utils.PropertyUtils;
import org.aron.mybatis.core.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-03-26 22:33
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Configuration configuration) {
        return new SqlSessionFactory(configuration);
    }

    public SqlSessionFactory build(Map<String, String> config) {
        Configuration configuration = new Configuration();
        configuration.initialize(config);
        return new SqlSessionFactory(configuration);
    }

    public SqlSessionFactory build(InputStream inputStream) {
        Configuration configuration = new Configuration();
        try {
            Map<String, String> map = PropertyUtils.load(inputStream);
            configuration.initialize(map);
            return new SqlSessionFactory(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
