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

/**
 * @author: Y-Aron
 * @create: 2019-03-30 01:48
 */
@Slf4j
public class TestUpdate {
    private TestMapper mapper;
    private TestEntity testEntity;

    @Before
    public void makeMapper() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder()
                .build(TestSelect.class.getResourceAsStream("/application.properties"));
        SqlSession sqlSession;
        try {
            sqlSession = factory.openSession();
            mapper = sqlSession.getMapper(TestMapper.class);
        } catch (NoSuchFieldException | SQLException e) {
            e.printStackTrace();
        }
        testEntity = new TestEntity();
        testEntity.setName("插入一条数据:更新");
        testEntity.setDesc("描述");
    }

    @Test
    public void test() {
        int row = mapper.updateOne(5, testEntity);
        log.debug("{}", row);
    }

    @Test
    public void testPlus() {
        int row = mapper.updateOnePlus(testEntity, new long[]{5,6,7});
        log.debug("{}", row);
    }

    @Test
    public void testUpdateProvider() {
        int row = mapper.updateProvider(9, testEntity);
        log.debug("{}", row);
    }
}
