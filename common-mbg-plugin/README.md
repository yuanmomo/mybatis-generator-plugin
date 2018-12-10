Common MBG Plugin
=======================

# Introduction
A few plugins to enhance the default MyBatis Generator. 

### Plugins list
 * Pagination.
 * A batchInsert method.
 * A selectOneByExample method etc.
 * The generated Java class file merge plugin.

  All the plugins are very useful to me. Especially the Java class file merge.

###&lt;table&gt; element generator

To run MBG(MyBatis Generator), a generatorConfig.xml file is needed. However, each table has to config an xml element of **&lt;table&gt;&lt;/table&gt;** in generatorConfig.xml. When it comes to lots of tables, it will cost a lot of time to write this **&lt;table&gt;&lt;/table&gt;** configuration.
Here I provide a tool to generate these configurations for all the tables in single one database.

# Requirements

* JDK  1.7+
* mybatis-generator-core  1.3.5 + 
* mybatis  3.4.4 + 

# Usage

### Plugin configuration
```xml
<generatorConfiguration>
    ........
    <context>
        ......

        <!-- add an extra selectOneByExample method into mapper class -->
        <plugin type="net.yuanmomo.mybatis.mbg.plugin.selectone.MapperAddSelectOneByExamplePlugin">
            <property name="methodName" value="getOneByExample"/>
        </plugin>

        <!-- add an extra selectOneByExample method into mapper class -->
        <plugin type="net.yuanmomo.mybatis.mbg.plugin.batch.MySqlBatchInsertPlugin">
            <property name="methodName" value="batchInsert"/>
        </plugin>

        <!-- Pagination plugin. -->
        <plugin type="net.yuanmomo.mybatis.mbg.plugin.PaginationPlugin"/>

        <!-- Extra DAO interface. -->
        <plugin type="net.yuanmomo.mybatis.mbg.plugin.DAOPlugin">
            <property name="typeName" value="DAO"/>
        </plugin>
        
		......
		<table>
			........
		</table>
		<table>
			........
		</table>
    </context>
    
    <!-- multi databases -->
    <context>
    	.........
    </context>
</generatorConfiguration>
```
The demonstration of the configuration file [generatorConfig.xml](https://github.com/yuanmomo/mybatis-generator-plugin/blob/master/common-mbg-plugin/src/test/resources/generatorConfig.xml)
 
### Table element generator

Generator the &lt;table&gt;&lt;/table&gt; xml code above.

```java
public class MySqlXMLGeneratorTest {
    @Test
	public void generate(){
		/**
		 * default host : 127.0.0.1
		 * defult port: 3306
		 */
		MySqlTableXmlPrinter mySqlTableXmlPrinter1 = new MySqlTableXmlPrinter("root","root");

		MySqlTableXmlPrinter mySqlTableXmlPrinter2 = new MySqlTableXmlPrinter("host","port","username","password");
		
		// three databases named : test, demo, todo
		List<String> output = mySqlTableXmlPrinter1.print("test","demo","todo");
		for(String str : output){
			System.out.println(str);
		}
	}
}
```
### MBG run tool
```java
public class GeneratorMainTest {
	@Test
	public void testMBGGenerator() {
		// path of generatorConfig.xml
		MyBatisGeneratorTool.generate("src/test/resources/generatorConfig.xml");
	}
}
```
# More
More demonstrations can be found [here](https://github.com/yuanmomo/mybatis-generator-plugin/tree/master/common-mbg-plugin/src/test)



