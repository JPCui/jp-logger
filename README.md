# Introduce
	
	由于项目做了简单的分布，为了便于管理系统运行日志，使用log4mongo将日志存储到mongodb上
	
	暂时只记录系统运行日志，所以没有用kafka, ELK这些比较重的框架
	
	为了日志代码部署与主系统独立，将日志的代码独立了出来。
	
	该项目使用SpringBoot搭建，所以使用起来非常简单。
	
	主系统中将日志系统上传到redis队列中（lpush），再由本系统监听队列（brpop），最后上传到mongodb上。
	
	目前存在的问题：
		
		NodeConsumer 不支持高并发，因为一个节点要增量更新，可以通过使用更高级的 Mongo API （findAndUpdate） 来解决。
	
# Environment & Frameworks

- java8, spring boot, maven
- freemarker
- jquery, angularjs
- redis
- mongodb
	
# Java Config

- MongoConfig
	配置 mongo client

- RedisConfig
	注意：我这里redis服务器部署了sentinel

- MVCConfig
	配置MVC，取代application.properties中的部分配置

# Run it

- 运行*cn.cjp.logger.service.LogServiceTest#report()*，添加测试数据

- 运行*cn.cjp.logger.Application*启动项目：

    - 页面1：

    访问：/jp-logger/log/info
    
    这是一个简单的查看页面

    - 页面2：

    访问：/jp-logger/log/inspector
    
    这里异步加载 */jp-logger/log/inspector.json* 来显示到页面上

## Others

- LogConsumer

- NodeConsumer

- cn.cjp.logger.service.BeanInspector
	call tree 核心类（AOP）
