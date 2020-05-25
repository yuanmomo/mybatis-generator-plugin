package com.github.yuanmomo.test.generator.mybatis.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import com.github.yuanmomo.test.generator.bean.ToDo;
import com.github.yuanmomo.test.generator.bean.ToDoParam;

public interface ToDoMapper {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @SelectProvider(type = ToDoSqlProvider.class, method = "countByExample")
    long countByExample(@Param("tableName") String tableName, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @DeleteProvider(type = ToDoSqlProvider.class, method = "deleteByExample")
    int deleteByExample(@Param("tableName") String tableName, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Delete({ "delete from ${tableName}", "where id = #{id,jdbcType=BIGINT}" })
    int deleteByPrimaryKey(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Insert({ "insert into ${tableName} (to_do, version, ", "remark, long_text)", "values (#{record.toDo,jdbcType=INTEGER}, #{record.version,jdbcType=BIGINT}, ", "#{record.remark,jdbcType=VARCHAR}, #{record.longText,jdbcType=LONGVARCHAR})" })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(@Param("tableName") String tableName, @Param("record") ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @InsertProvider(type = ToDoSqlProvider.class, method = "insertSelective")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insertSelective(@Param("tableName") String tableName, @Param("record") ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @SelectProvider(type = ToDoSqlProvider.class, method = "selectByExampleWithBLOBs")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR), @Result(column = "long_text", property = "longText", jdbcType = JdbcType.LONGVARCHAR) })
    List<ToDo> selectByExampleWithBLOBs(@Param("tableName") String tableName, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @SelectProvider(type = ToDoSqlProvider.class, method = "selectByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR) })
    List<ToDo> selectByExample(@Param("tableName") String tableName, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Select({ "select", "id, to_do, version, remark, long_text", "from ${tableName}", "where id = #{id,jdbcType=BIGINT}" })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR), @Result(column = "long_text", property = "longText", jdbcType = JdbcType.LONGVARCHAR) })
    ToDo selectByPrimaryKey(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @UpdateProvider(type = ToDoSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("tableName") String tableName, @Param("record") ToDo record, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @UpdateProvider(type = ToDoSqlProvider.class, method = "updateByExampleWithBLOBs")
    int updateByExampleWithBLOBs(@Param("tableName") String tableName, @Param("record") ToDo record, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @UpdateProvider(type = ToDoSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("tableName") String tableName, @Param("record") ToDo record, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @UpdateProvider(type = ToDoSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(@Param("tableName") String tableName, @Param("record") ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Update({ "update ${tableName}", "set to_do = #{record.toDo,jdbcType=INTEGER},", "version = #{record.version,jdbcType=BIGINT},", "remark = #{record.remark,jdbcType=VARCHAR},", "long_text = #{record.longText,jdbcType=LONGVARCHAR}", "where id = #{record.id,jdbcType=BIGINT}" })
    int updateByPrimaryKeyWithBLOBs(@Param("tableName") String tableName, @Param("record") ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Update({ "update ${tableName}", "set to_do = #{record.toDo,jdbcType=INTEGER},", "version = #{record.version,jdbcType=BIGINT},", "remark = #{record.remark,jdbcType=VARCHAR}", "where id = #{record.id,jdbcType=BIGINT}" })
    int updateByPrimaryKey(@Param("tableName") String tableName, @Param("record") ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @SelectProvider(type = ToDoSqlProvider.class, method = "getOneByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR), @Result(column = "long_text", property = "longText", jdbcType = JdbcType.LONGVARCHAR) })
    Optional<ToDo> getOneByExample(@Param("tableName") String tableName, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Options(useGeneratedKeys = true, keyProperty = "list.id", keyColumn = "id")
    @Insert({ "<script>", "insert into ${tableName} (to_do, ", "version, remark, ", "long_text)", "values<foreach collection=\"list\" item=\"detail\" index=\"index\" separator=\",\">(#{detail.toDo,jdbcType=INTEGER}, ", "#{detail.version,jdbcType=BIGINT}, #{detail.remark,jdbcType=VARCHAR}, ", "#{detail.longText,jdbcType=LONGVARCHAR})</foreach></script>" })
    int batchInsert(@Param("tableName") String tableName, @Param("list") List<ToDo> list);
}
