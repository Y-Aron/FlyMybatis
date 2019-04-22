package org.aron.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.aron.commons.utils.Utils;
import org.aron.mybatis.core.Configuration;
import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.core.session.SqlSessionFactory;
import org.aron.mybatis.core.session.SqlSessionFactoryBuilder;
import org.aron.mybatis.core.transaction.Transaction;
import org.aron.mybatis.core.transaction.impl.JDBCTransaction;
import org.aron.mybatis.entity.TestEntity;
import org.aron.mybatis.jdbc.DataSourceFactory;
import org.aron.mybatis.jdbc.Environment;
import org.aron.mybatis.jdbc.bean.JDBCBean;
import org.aron.mybatis.mapper.master.TestMapper;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: Y-Aron
 * @create: 2019-03-26 23:02
 */
@Slf4j
public class App {

    @Test
    public void test2() throws NoSuchFieldException, SQLException {
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(App.class.getResourceAsStream("/application.properties"));
        SqlSession sqlSession = sessionFactory.openSession();
        TestMapper master = sqlSession.getMapper(TestMapper.class);
        List<TestEntity> masterList = master.select();
        masterList.forEach(vol -> log.debug("master: {}", vol));

        log.debug("===============================================");
        sqlSession.setEnvironment("slave");
//        SqlSession slaveSqlSession = sessionFactory.openSession("slave");
        org.aron.mybatis.mapper.slave.TestMapper slave = sqlSession.getMapper(org.aron.mybatis.mapper.slave.TestMapper.class);
        List<TestEntity> slaveList = slave.select();
        slaveList.forEach(vol -> log.debug("slave: {}", vol));
    }


    @Test
    public void test1() throws NoSuchFieldException, SQLException {
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(App.class.getResourceAsStream("/application.properties"));
        SqlSession sqlSession = sessionFactory.openSession();
        sqlSession.transaction();
        try {
            TestMapper mapper = sqlSession.getMapper(TestMapper.class);
            mapper.insertOne(new TestEntity(){{
                setName("name" + Utils.generateUUID());
                setDesc("desc" + Utils.generateUUID());
            }});
            mapper.insertOne(new TestEntity(){{
                setName("name" + Utils.generateUUID());
                setDesc("desc" + Utils.generateUUID());
            }});
//            int a = 1/0;
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
        }
    }

    @Test
    public void test() throws NoSuchFieldException, SQLException {
        DataSource dataSource = DataSourceFactory.build(new JDBCBean(){{
            setDriverClass("com.mysql.cj.jdbc.Driver");
            setUrl("jdbc:mysql://127.0.0.1:3306/community?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT");
            setUsername("root");
            setPassword("admin");
            setInitSize(5);
            setMinSize(5);
            setMaxSize(100);
        }});
        Transaction transaction = new JDBCTransaction();
        Environment environment = new Environment("name", transaction, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.setProxyMode("cglib");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        SqlSession sqlSession = sessionFactory.openSession();
//        TestMapper mapper = sqlSession.getMapper(TestMapper.class);
//        log.trace("{}", mapper);
//        List<TestEntity> userList = mapper.select();
//        userList.forEach(user -> log.trace("user: {}", user));
    }
}
