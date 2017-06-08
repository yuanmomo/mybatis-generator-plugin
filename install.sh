echo 说 明
echo --------------------------------------------------------------------
echo @Copyright 2017 Create By Hongbin.Yuan  QQ:342398690 日期：2017-06-02 22:59:00

echo 开始编译 framework
mvn -DskipTests=true install

echo 创建 spring-boot-famework 项目骨架
mvn  clean archetype:create-from-project
cd target/generated-sources/archetype
mvn  install
cd ../../../

echo 清理项目
mvn clean
