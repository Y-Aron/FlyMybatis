package org.aron.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.aron.mybatis.core.session.SqlSession;
import org.aron.mybatis.core.session.SqlSessionFactory;
import org.aron.mybatis.core.session.SqlSessionFactoryBuilder;
import org.aron.mybatis.entity.TestEntity;
import org.aron.mybatis.mapper.master.TestMapper;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Y-Aron
 * @create 2019/3/28
 */
@Slf4j
public class TestSelect {

    private TestMapper master;

    @Before
    public void makeMapper() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder()
                .build(TestSelect.class.getResourceAsStream("/application.properties"));
        SqlSession sqlSession;
        try {
            sqlSession = factory.openSession();
            master = sqlSession.getMapper(TestMapper.class);
        } catch (NoSuchFieldException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        List<TestEntity> list = master.select();
        list.forEach(vol -> log.debug("{}", vol));
    }

    @Test
    public void test1() {
        TestEntity test = master.selectOne();
        log.debug("{}", test);
    }

    @Test
    public void test2() {
        TestEntity testEntity = new TestEntity();
        testEntity.setId(8);
        TestEntity test = master.selectOne(testEntity);
        log.debug("{}", test);
    }

    @Test
    public void test3() {
        TestEntity testEntity = new TestEntity();
        testEntity.setName("name");
        List<TestEntity> list = master.select(testEntity);
        list.forEach(vol -> log.debug("{}", vol));
    }

    @Test
    public void test4() {
        TestEntity testEntity = new TestEntity();
        testEntity.setName("name");
        Map<String, Object> map = master.selectMap(testEntity);
        map.forEach((k, v) -> log.debug("k: {}, v: {}", k, v));
    }

    @Test
    public void test5() {
        TestEntity testEntity = new TestEntity();
        testEntity.setName("name");
        List<Map<String, Object>> listMap = master.selectListMap(testEntity);
        listMap.forEach(vol -> log.debug("{}", vol));
    }

    @Test
    public void test6() {
        TestEntity testEntity = new TestEntity();
        testEntity.setName("name");
        List<TestEntity> list = master.selectProvider(testEntity);
        list.forEach(vol -> log.debug("{}", vol));
    }

}
