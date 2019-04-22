package org.aron.mybatis.mapper.slave.provider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aron.mybatis.annotation.Param;
import org.aron.mybatis.entity.TestEntity;


/**
 * @author: Y-Aron
 * @create: 2019-01-20 14:21
 **/
@Slf4j
public class TestMapperProvider {

    public String testSelect(TestEntity test) {
        log.info("----------test select----------");
        StringBuilder sb = new StringBuilder();
        sb.append("select * from tb_test where ");
        if (test.getId() > 0) {
            sb.append("id=#{test.id} and ");
        }
        if (StringUtils.isNotBlank(test.getName())) {
            sb.append("`name`=#{test.name}");
        }
        if (StringUtils.isNotBlank(test.getDesc())) {
            sb.append("`desc`=#{test.desc}");
        }
        return sb.toString();
    }

    public String testInsert(@Param("t") TestEntity test) {
        return "insert into tb_test(id, `name`, `desc`) values(#{t.id}, #{t.name}, #{t.desc})";
    }

    public String testUpdate(long id, TestEntity test) {
        return "update tb_test set `name`=#{test.name} where id=#{id}";
    }

    public String testDelete(long id) {
        return "delete from tb_test where id=#{id}";
    }
}
