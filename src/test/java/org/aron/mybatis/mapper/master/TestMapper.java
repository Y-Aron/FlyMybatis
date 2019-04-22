package org.aron.mybatis.mapper.master;

import org.aron.mybatis.annotation.Param;
import org.aron.mybatis.annotation.provider.DeleteProvider;
import org.aron.mybatis.annotation.provider.InsertProvider;
import org.aron.mybatis.annotation.provider.SelectProvider;
import org.aron.mybatis.annotation.provider.UpdateProvider;
import org.aron.mybatis.annotation.sql.Delete;
import org.aron.mybatis.annotation.sql.Insert;
import org.aron.mybatis.annotation.sql.Select;
import org.aron.mybatis.annotation.sql.Update;
import org.aron.mybatis.entity.TestEntity;
import org.aron.mybatis.mapper.master.provider.TestMapperProvider;

import java.util.List;
import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-01-12 18:16
 **/
public interface TestMapper {

    /**
     * 抽象方法或接口无法根据asm获取方法参数名
     * jdk1.8以上可以使用 -parameters 参数编译
     * 或者设置@Param注解定义
     * 如果方法是自定义类的话，则属性要与自定义类中的字段名一致
     */
    @Insert("insert into tb_test(`id`, `name`, `desc`) values(#{test.id}, #{test.name}, #{test.desc})")
    int insertOne(@Param("test") TestEntity user);

    /**
     * @Insert 增强注解
     * 当value为空值 使用增强型注解
     * table -> 数据库表名
     * fields -> 数据库表字段
     */
    @Insert(table = "tb_test", fields = {"nickname", "name", "desc"})
    int insertOne1(TestEntity test, String nickname);

    @InsertProvider(type = TestMapperProvider.class, method = "testInsert")
    int insertProvider(TestEntity user);

    @DeleteProvider(type = TestMapperProvider.class, method = "testDelete")
    int deleteProvider(long id);

    @UpdateProvider(type = TestMapperProvider.class, method = "testUpdate")
    int updateProvider(long id, TestEntity tt);

    @Delete("delete from tb_test where id=#{id}")
    int deleteOne(long id);

    @Delete(table = "tb_test", and = {"id"})
    int deleteOnePlus(long id);

    @Update("update tb_test set `name`=#{test.name} where id=#{id}")
    int updateOne(long id, TestEntity test);

    @Update(table = "tb_test", and = {"id"}, fields = {"name", "desc"})
    int updateOnePlus(TestEntity test, long[] id);

    @Select("select * from tb_test")
    List<TestEntity> select();

    @Select("select * from tb_test where name=#{t.name}")
    List<TestEntity> select(@Param("t") TestEntity user);

    @Select("select * from tb_test")
    TestEntity selectOne();

    @Select("select * from tb_test where id=#{test.id}")
    TestEntity selectOne(TestEntity test);

    @Select("select name from tb_test where name=#{test.name}")
    Map<String, Object> selectMap(TestEntity test);

    @Select("select `name`, `desc` from tb_test where name=#{test.name}")
    List<Map<String, Object>> selectListMap(TestEntity test);

    @SelectProvider(type = TestMapperProvider.class, method = "testSelect")
    List<TestEntity> selectProvider(TestEntity user);
}