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
 * @create: 2019-03-30 00:27
 */
@Slf4j
public class TestDelete {

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
        testEntity.setName("插入一条数据");
        testEntity.setDesc("描述");
    }

    @Test
    public void test() {
        int row = mapper.deleteOne(46L);
        log.debug("{}", row);
    }

    @Test
    public void testPlus() {
        int row = mapper.deleteOnePlus(4);
        log.debug("{}", row);
    }

    @Test
    public void testDeleteProvider() {
        int row = mapper.deleteProvider(73);
        log.debug("{}", row);
    }
}
