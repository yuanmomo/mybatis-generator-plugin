MyBatis Generator Plugin
=======================

# 简介
MyBatis Generator Plugin 是一个官方 MyBatis Generator(MBG) 的插件。通过自定义一些插件，满足一些定制化的需求。

# 扩展功能
## 自动合并（auto merge）
对于使用 MBG 生成的 mapper interface 接口文件中，如果添加了自定义的扩展方法，再次执行 MBG 生成后（ DB 新增字段），新生成的 mapper interface 会覆盖掉原有的文件，导致自定义方法的丢失。

如果使用 MyBatis Generator Plugin 插件提供的生成方法时，会自动合并原有的文件和新生成的文件，保证自定义的方法和属性不丢失。

## 增加 getOneByExample 方法
默认使用 MBG 生成的 mapper interface 的方法中，使用 Example 参数查询时，默认会返回的是一个 List<Object>，如果此时业务数据是唯一时，总是需要判断  List 是否为空，同时从 List.get(0) 获取数据，比较麻烦。

此时可以直接使用 getOneByExample 方法，直接通过 Example 参数查询，返回一条记录。

>**提示：**
>
> * 新版本的 MBG 的 Dynamic SQL 已经支持 `selectOne` 方法。


## 批量插入
在 mapper interface 中增加一个批量插入的方法 `batchInsert`。

## 分页插件
MyBatis 自带的分页是内存分页（假分页），导致性能很低，所以需要使用真分页支持。

MyBatis Generator Plugin 插件生成的 Example 类中会增加两个参数，start 和 count，在执行 select 时，会根据这两个参数进行分页查询。

## All Columns Fields 
设想一种场景：
1. 数据库此时有 3 个字段：a，b，c；

2. 当前的 mapper 中有一个自定义方法 `getOneByXXXX` 方法， SQL语句为：

    ```SQL
    select a,b,c from table where .....
    ```

3. 代码中有一个更新的逻辑，而实现是先调用了 `getOneByXXXX` 方法查询到记录，修改相应字段，然后再全量 update 到 db。
4. 此时，某次业务在表中新增了一个字段 d，而新增字段的开发人员，没有去修改上面 `getOneByXXXX` 方法中 `select` 语句中的字段；
5. 此时，第 3 步的更新操作就会出现问题。因为 select 出来的结果，没有包含 d 字段值，所以在更新的时候，可能会导致 d 字段的值被覆写，导致数据丢失。

> *曾经赤裸裸的被同事坑过，然后查了好些时间才查到这个问题。*

由于上面的场景，所以在生成的 Provider 类中增加了一个 `ALL_COLUMN_FIELDS` 字段，包含了所有的表的列。如果使用全列时，使用这个字段就可以避免上面的问题。

## Lombok 支持
可以在生成的 Bean 对象上增加 Lombok 的 Annotation：
* @Data
* @EqualsAndHashCode
* @ToString
* @NoArgsConstructor

## 简化生成类
该插件可以屏蔽 MBG 生成的 Exapmle 相关的信息，简化生成的文件，包括：
* Example 类
* Mapper 中跟 Example 相关的方法
* Provider 中跟 Example 相关的方法

默认生成的 Mapper 类仅包含以下方法：

```Java
// 根据主键删除记录
int deleteByPrimaryKey(Integer id);
// 动态插入对象的非空字段
int insertSelective(<Bean> record);
// 根据主键查询记录
<Bean> selectByPrimaryKey(Integer id);
// 根据主键更新对象的非空字段
int updateByPrimaryKeySelective(<Bean> record);
// 批量插入
int batchInsert(java.util.List<<Bean>> list)
```

## 动态表支持
针对一张表生成的 Mapper 和 Provider，支持动态传入表名。适用于一个 DB 存在多个结构相同，但是表名不同的场景。

>**注意：**
>
> * 如果使用了动态表插件，那么调用 `insertSelective` 方法时，为了保证 `LAST_INSERT_ID`功能依然能够正常使用（就是插入后，自动获取最新 id，然后注入到插入对象），在插入直接需要调用 Bean 对象的 `setDynamicTableName(String dynamicTableName)` 方法，将表名设置到对象的属性中。


# 使用方法

## 使用建议
**可以在项目中的 .gitignore 忽略下面的生成配置文件和生成工具，由单人生成文件和代码后提交**

**这样可以不影响现有代码，无侵入使用。**
 
## 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.github.yuanmomo</groupId>
    <artifactId>mybatis-generator-plugin</artifactId>
    <version>1.0.11</version>
    <scope>compile</scope>
</dependency>
```

## 添加 `mbg.properties` 文件

```property
# 生成的文件路径，一般为 src/main/java
mbg.targetProject=src/main/java

# jdbc 配置，主要连接和库名
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/mbg
username=root
password=root

# 生成的 bean 类的包名
mbg.modelPackage=com.github.yuanmomo.mybatis.generator.test.bean

# 生成的 Mapper 接口的包名
mbg.javaClientPackage=com.github.yuanmomo.mybatis.generator.test.mapper
```

## 编写生成的方法调用类
```Java

import lombok.extern.slf4j.Slf4j;

/**
 *
 */

@Slf4j
public class GenerateTool {
      /**
     * 生成 generatorConfig.xml 文件中，每张表的具体配置。
     * @throws IOException
     */
    @Test
    public void generatorConfigXml() throws IOException {
        // mbg.properties
        // 指定数据库 mbg
        List<String> output = TableXMLPrinter.print("src/main/resources/mbg.properties","mbg");
        for(String str : output){
            System.out.println(str);
        }
    }

    /**
     * 通过 generatorConfig.xml 配置文件生成 Bean，Mapper 和 Provider。
     */
    @Test
    public void mbgGenerate() {
        MyBatisGeneratorTool.generate("src/main/resources/generatorConfig.xml");
    }
}
```
1. 执行 generatorConfigXml 方法后，复制生成的 `<table>` 配置，粘贴到 `generatorConfig.xml` 文件的 `<table>` 配置处。

2. 粘贴后，修改动态表名配置。


## 添加 generatorConfig.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
    PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
    "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>

<!-- 加载 mbg.properties 文件  -->
<properties resource="mbg.properties"/>

<context id="mbg_schema" targetRuntime="MyBatis3" defaultModelType="conditional">

 
    <!-- 生成的实体类（Bean）自动继承 java.io.Serializable interface. -->
    <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>

    <!-- ######################### 扩展插件列表 ########################## -->

    <!-- 生成的实体类（Bean）增加 Lombok 注解. -->
    <plugin type="com.github.yuanmomo.mybatis.mbg.plugin.LombokPlugin">
        <property name="data" value="true"/>
        <property name="hashEquals" value="true"/>
        <property name="toString" value="true"/>
        <property name="noArgConstructor" value="true"/>
    </plugin>

    <!-- 在 Provider 类增加 All Columns 属性 . -->
    <plugin type="com.github.yuanmomo.mybatis.mbg.plugin.allfields.AllColumnsFieldsPlugin"/>
    
    <!-- 在 Mapper 接口增加 batchInsert 方法 -->
    <plugin type="com.github.yuanmomo.mybatis.mbg.plugin.batch.MySqlBatchInsertPlugin">
        <property name="methodName" value="batchInsert"/>
    </plugin>

    <!-- 支持动态表名插件 -->
    <plugin type="com.github.yuanmomo.mybatis.mbg.plugin.dynamic.DynamicTableNamePlugin"/>
    
    
    <!-- 简化生成类. -->
    <plugin type="com.github.yuanmomo.mybatis.mbg.plugin.SimplePlugin" />

    <!-- 在 Mapper 接口增加 getOneByExample 方法 -->
    <!-- 如果启用了《简化生成类》插件，该插件不生效 -->
    <plugin type="com.github.yuanmomo.mybatis.mbg.plugin.getone.MapperAddGetOneByExamplePlugin">
        <property name="methodName" value="getOneByExample"/>
    </plugin>

    <!-- 增加分页支持 -->
    <!-- 如果启用了《简化生成类》插件，该插件不生效 -->
    <plugin type="com.github.yuanmomo.mybatis.mbg.plugin.PaginationPlugin"/>

    
    <!-- 在生成的注解中去掉日期，防止生成后，因为日志变更导致需要 git 提交 -->
    <commentGenerator>
        <property name="suppressDate" value="true"/>
    </commentGenerator>

    <!-- jdbc 配置 -->
    <jdbcConnection driverClass="${driver}" connectionURL="${url}"
                    userId="${username}" password="${password}" />

    <!-- Java 类型转换的配置 -->
    <javaTypeResolver>
        <property name="forceBigDecimals" value="false"/>
    </javaTypeResolver>

    <!-- 生成的 Java Bean 配置 -->
    <javaModelGenerator targetPackage="${mbg.modelPackage}"
                        targetProject="${mbg.targetProject}" />

    <!-- 生成基于 Annotation 的 Mapper 接口和 Provider 类 -->
    <javaClientGenerator type="ANNOTATEDMAPPER"
                         targetPackage="${mbg.javaClientPackage}" targetProject="${mbg.targetProject}">
        <property name="enableSubPackages" value="true"/>
    </javaClientGenerator>

    <!-- 1. 粘贴前面执行 generatorConfigXml 方法生成的配置 -->
    <!-- 2. 动态表名配置 -->
    <table tableName="mbg_table" domainObjectName="MbgTable">            
        <!-- 自增长主键 -->
        <generatedKey column="id" sqlStatement="MySql" identity="true"/>
    </table>
    <!-- 动态表名示范 -->
    <table tableName="mbg_table" domainObjectName="DynamicTable">
        <!-- 如果该表需要动态传入表名，设置 dynamic 为 true -->
        <property name="dynamic" value="true"/>
        
        <!-- 自增长主键 -->
        <generatedKey column="id" sqlStatement="MySql" identity="true"/>
    </table>
    
    <!-- ... 多张表配置 ... -->
</context>
</generatorConfiguration>
```

执行 `mbgGenerate` 方法，生成的结构如下：

![mbg-generate-result](https://img.tupm.net/2020/06/556435D235E39B63551EBD4C343E59BE.jpg)

# 生成结果
## Bean 类
![mbg-bean](https://img.tupm.net/2020/06/11743FE645FAC1A8458EBDAB372DDE5F.jpg)

## Mapper
![mbg-mapper](https://img.tupm.net/2020/06/3EF6DAEAC6732719D92B48265B1F5E14.jpg)

## 动态表名
### Mapper 接口
![mappder-dynamic-table-name](https://img.tupm.net/2020/06/7D7CFA300B2214D1C18CB3E164BAF3B3.jpg)

### Provider 类
![provider-dynamic-table-name](https://img.tupm.net/2020/06/35D2D9D32A20E0F67EDB0DB1689AB037.jpg)

# 使用示例

参考：[https://github.com/yuanmomo/mybatis-generator-plugin-demo](https://github.com/yuanmomo/mybatis-generator-plugin-demo) 

