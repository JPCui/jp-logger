# introduce
```
# 配置mongodb
cn.cjp.logger.mongo.MongoConfig

# 配置redis
# 注意：我这里redis服务器部署了sentinel
cn.cjp.logger.redis.RedisConfig

# 配置MVC，取代application.properties中的配置
cn.cjp.logger.web.controller.MVCConfig
```

运行*cn.cjp.logger.service.LogServiceTest#report()*，添加测试数据

- 测试1：

访问：http://local:8080/log/DEBUG

这是一个简单的查看页面

- 测试2：

访问：http://local:8080/pages/index.html

这里异步加载*http://local:8080/log/DEBUG.json*来显示到页面上


# how to build an executable war

	"这关系到项目独立运行与发布"，这里提供两种方案

1. runnable war

- add <plugin> below to ++pom.xml++
```xml
<plugin>
	<groupId>org.apache.tomcat.maven</groupId>
	<artifactId>tomcat7-maven-plugin</artifactId>
	<version>2.1</version>
	<executions>
		<execution>
		<id>tomcat-run</id>
			<goals>
				<goal>exec-war-only</goal>
			</goals>
			<phase>package</phase>
			<configuration>
				<path>/</path>
				<!-- optional only if you want to use a preconfigured server.xml file -->
				<!-- 
				<serverXml>src/main/tomcatconf/server.xml</serverXml>
				 -->
				<!-- optional values which can be configurable -->
				<attachArtifactClassifier>default value is exec-war but you can
					customize</attachArtifactClassifier>
				<attachArtifactClassifierType>default value is jar</attachArtifactClassifierType>
			</configuration>
		</execution>
	</executions>
</plugin>
```

- run the following command:
```
mvn clean package -Prunnable-war
```

2. 使用maven插件对java工程进行打包

- package jar with dependencies

```xml
<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <configuration>
        <appendAssemblyId>false</appendAssemblyId>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
        <archive>
            <manifest>
                <mainClass>com.chenzhou.examples.Main</mainClass>
            </manifest>
        </archive>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>assembly</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

- run the following command:
```
mvn assembly:assembly -Dmaven.test.skip=true
```

# referrence

- [executable war jar](http://tomcat.apache.org/maven-plugin-trunk/executable-war-jar.html)
- [How to Build an Executable War or Jar Files](http://nextcoder.com/?p=1351)
- [使用maven插件对java工程进行打包](http://chenzhou123520.iteye.com/blog/1706242)
