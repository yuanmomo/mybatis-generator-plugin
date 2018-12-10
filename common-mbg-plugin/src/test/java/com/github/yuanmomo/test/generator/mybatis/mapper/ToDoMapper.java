package com.github.yuanmomo.test.generator.mybatis.mapper;

import java.util.List;
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

    @Select({ "select", ToDoSqlProvider.ALL_COLUMN_FIELDS, "from table_to_do", "where id = #{id,jdbcType=BIGINT}" })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR), @Result(column = "li_bin_hui", property = "liBinHui", jdbcType = JdbcType.BIGINT) })
    ToDo select(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @SelectProvider(type = ToDoSqlProvider.class, method = "countByExample")
    long countByExample(ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @DeleteProvider(type = ToDoSqlProvider.class, method = "deleteByExample")
    int deleteByExample(ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Delete({ "delete from table_to_do", "where id = #{id,jdbcType=BIGINT}" })
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Insert({ "insert into table_to_do (to_do, version, ", "remark, li_bin_hui)", "values (#{toDo,jdbcType=INTEGER}, #{version,jdbcType=BIGINT}, ", "#{remark,jdbcType=VARCHAR}, #{liBinHui,jdbcType=BIGINT})" })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @InsertProvider(type = ToDoSqlProvider.class, method = "insertSelective")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insertSelective(ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @SelectProvider(type = ToDoSqlProvider.class, method = "selectByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR), @Result(column = "li_bin_hui", property = "liBinHui", jdbcType = JdbcType.BIGINT) })
    List<ToDo> selectByExample(ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Select({ "select", "id, to_do, version, remark, li_bin_hui", "from table_to_do", "where id = #{id,jdbcType=BIGINT}" })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR), @Result(column = "li_bin_hui", property = "liBinHui", jdbcType = JdbcType.BIGINT) })
    ToDo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @UpdateProvider(type = ToDoSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") ToDo record, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @UpdateProvider(type = ToDoSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") ToDo record, @Param("example") ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @UpdateProvider(type = ToDoSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Update({ "update table_to_do", "set to_do = #{toDo,jdbcType=INTEGER},", "version = #{version,jdbcType=BIGINT},", "remark = #{remark,jdbcType=VARCHAR},", "li_bin_hui = #{liBinHui,jdbcType=BIGINT}", "where id = #{id,jdbcType=BIGINT}" })
    int updateByPrimaryKey(ToDo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @SelectProvider(type = ToDoSqlProvider.class, method = "getOneByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true), @Result(column = "to_do", property = "toDo", jdbcType = JdbcType.INTEGER), @Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT), @Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR), @Result(column = "li_bin_hui", property = "liBinHui", jdbcType = JdbcType.BIGINT) })
    ToDo selectOneByExample(ToDoParam example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table table_to_do
     *
     * @mbg.generated
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert({ "<script>", "insert into table_to_do (to_do, ", "version, remark, ", "li_bin_hui)", "values<foreach collection=\"list\" item=\"detail\" index=\"index\" separator=\",\">(#{detail.toDo,jdbcType=INTEGER}, ", "#{detail.version,jdbcType=BIGINT}, #{detail.remark,jdbcType=VARCHAR}, ", "#{detail.liBinHui,jdbcType=BIGINT})</foreach></script>" })
    int batchInsert(List<ToDo> list);
}
