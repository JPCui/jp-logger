# introduce
	
	由于项目做了分布，为了便于管理系统运行日志，使用log4mongo将日志存储到mongodb上
	
	暂时只记录系统运行日志，所以没有用kafka, ELK这些比较重的框架
	
	为了日志代码部署与主系统独立，将日志的代码独立了出来。
	
	该项目使用SpringBoot搭建，所以使用起来非常简单。
	
	主系统中将日志系统上传到redis队列中（lpush），再由本系统监听队列（brpop），最后上传到mongodb上。
	
# frameworks

- java7/8, spring boot, maven
- angularjs
- redis
- mongodb
	
# java config

```
 # 配置mongodb
 cn.cjp.logger.mongo.MongoConfig

 # 配置redis
 # 注意：我这里redis服务器部署了sentinel
 cn.cjp.logger.redis.RedisConfig

 # 配置MVC，取代application.properties中的配置
 cn.cjp.logger.web.controller.MVCConfig
```

# 运行一下

- 运行*cn.cjp.logger.service.LogServiceTest#report()*，添加测试数据

- 运行*cn.cjp.logger.Application*启动项目：

    - 页面1：

    访问：http://local:8080/log/DEBUG
    
    这是一个简单的查看页面

    - 页面2：

    访问：http://local:8080/pages/index.html
    
    这里异步加载*http://local:8080/log/DEBUG.json*来显示到页面上

## 其他

- cn.cjp.logger.service.LogConsumerManager
    
    使用线程池监听(redis.brpop)日志队列


