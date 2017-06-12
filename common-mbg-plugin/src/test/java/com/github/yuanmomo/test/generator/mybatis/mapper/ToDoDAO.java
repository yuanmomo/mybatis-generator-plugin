package com.github.yuanmomo.test.generator.mybatis.mapper;

import com.github.yuanmomo.test.generator.bean.ToDoParam;
import org.apache.ibatis.annotations.SelectProvider;

public interface ToDoDAO {

    @SelectProvider(type = ToDoSqlProvider.class, method = "countByExample")
    long countByExample(ToDoParam example);
}
