# seckill
a ssm project

# 开发环境
IDEA + Maven + SSM框架 + MySQL + Redis

# 项目描述
模仿网购商品的秒杀

# 实现步骤
## DAO实体和接口编码
首先创建数据库，相关表的sql语句在main包下的sql包中已经给出。  
然后创建对应表的实体类，在main.java包下创建org.seckill.entity/dao包，entity包下创建一个Seckill.java实体类和一个SuccessKilled.java。
然后针对实体创建出对应dao层的接口，在org.seckill.dao包下创建Seckill.java和SuccessKilled.java的接口SuccessKilledDao。

## 基于MyBatis实现Dao
首先需要配置我们的MyBatis，在resources包下创建MyBatis全局配置文件mybatis-config.xml文件，resources包下创建mapper包—用来放MyBatis的sql的映射。
在浏览器中输入`http://mybatis.github.io/mybatis-3/zh/index.html`打开MyBatis的官网文档，点击左边的"入门"栏框，找到mybatis全局配置文件，
在这里有xml的一个规范，也就是它的一个xml约束，拷贝:

```
<?xml version="1.0" encoding="UTF-8" ?> 
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
```
到我们的项目mybatis全局配置文件mybatis-config.xml中，然后在全局配置文件中加入配置信息。

配置文件创建好后我们需要关注的是Dao接口该如何实现，mybatis为我们提供了mapper动态代理开发的方式为我们自动实现Dao的接口。
在mapper包下创建对应Dao接口的xml映射文件，里面用于编写我们操作数据库的sql语句，SeckillDao.xml和SuccessKilledDao.xml。

## myBatis整合Spring
在main.resources包下创建一个spring包，里面放置spring对Dao、Service、transaction的配置文件(所有spring相关配置）。

## 测试
### SeckillDao接口测试
这样我们便完成了Dao层编码的开发，接下来就可以利用junit进行我们Dao层编码的测试了。首先测试SeckillDao.java，利用IDEA快捷键`ctrl+shift+T`对SeckillDao.java进行测试,勾取接口中的方法，
然后IDEA会自动在test包的java包下为我们生成对SeckillDao.java中所有方法的测试类SeckillDaoTest.java,接着自己编写测试代码。
然后便可以在这个测试类中对SeckillDao接口的所有方法进行测试了。

### SuccessKilledDao接口测试
接下来进行SuccessKilledDao接口相关方法的测试，依旧使用IDEA快捷键`shift+command+T`快速生成其方法的相应测试类SuccessKilledDaoTest。

注：学习时参考了codingXiaxw's blog（http://codingxiaxw.cn/2016/11/27/53-maven-ssm-seckill-dao/） 来做电子笔记。

