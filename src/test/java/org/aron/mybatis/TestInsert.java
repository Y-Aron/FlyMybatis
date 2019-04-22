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
 * @author Y-Aron
 * @create 2019/3/28
 */
@Slf4j
public class TestInsert {

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
    public void testInsertOne() {
//        int row = mapper.insertOne(testEntity);
//        log.debug("{}", row);
        for (int i = 0; i < 10; i++) {
            testEntity.setId(i);
            testEntity.setName("name" + i);
            testEntity.setDesc("desc" + i);
            int row = mapper.insertOne(testEntity);
            log.debug("{}", row);
        }
    }

    @Test
    public void testInsertOne1() {
        for (int i = 0; i < 2; i++) {
            testEntity.setName("name" + i);
            testEntity.setDesc("desc" + i);
            int row = mapper.insertOne1(testEntity, "nickname" + i);
            log.debug("{}", row);
        }
    }

    @Test
    public void testInsertProvider() {
        for (int i = 0; i < 2; i++) {
            testEntity.setName("name" + i);
            testEntity.setDesc("desc" + i);
            int row = mapper.insertProvider(testEntity);
            log.debug("{}", row);
        }
    }

}
