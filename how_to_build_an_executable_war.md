
	介绍一下"项目独立运行与发布"，这里提供两种方案

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

> mvn clean package -Dskip.test=true

- run it

> java -jar jp-logger-1.1.0.Release-war-exec.jar

- 如果想修改端口，需要配置 server.xml

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
                <mainClass>xxx.Main</mainClass>
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
mvn clean assembly:assembly -Dmaven.test.skip=true
```

# pom 备份

```
<!-- 打包的时候，将依赖也打包 -->
<plugin>
	<artifactId>maven-assembly-plugin</artifactId>
	<configuration>
		<appendAssemblyId>false</appendAssemblyId>
		<descriptorRefs>
			<descriptorRef>jar-with-dependencies</descriptorRef>
		</descriptorRefs>
		<archive>
			<manifest>
				<mainClass>cn.cjp.logger.Application</mainClass>
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

<plugin>
	<groupId>org.apache.tomcat.maven</groupId>
	<artifactId>tomcat7-maven-plugin</artifactId>
	<version>2.2</version>
	<configuration>
		<uriEncoding>${project.build.sourceEncoding}</uriEncoding>
		<update>true</update>
		<url>${deploy.url}</url>
		<server>${deploy.server}</server>
		<ignorePackaging>true</ignorePackaging>
	</configuration>
	<executions>
		<execution>
			<id>tomcat-run</id>
			<goals>
				<goal>exec-war-only</goal>
			</goals>
			<phase>package</phase>
			<configuration>
				<path>jp-logger</path>
				<!-- optional only if you want to use a preconfigured server.xml file -->
				<!-- 目前修改端口，只有在server.xml里配置 -->
				<serverXml>src/main/resources/tomcat/server.xml</serverXml>
				<!-- optional values which can be configurable -->
				<!-- default value is exec-war but you can customize -->
				<attachArtifactClassifier>exe</attachArtifactClassifier>
				<attachArtifactClassifierType>jar</attachArtifactClassifierType>
			</configuration>
		</execution>
	</executions>
</plugin>
```

# referrence

- [executable war jar](http://tomcat.apache.org/maven-plugin-trunk/executable-war-jar.html)
- [How to Build an Executable War or Jar Files](http://nextcoder.com/?p=1351)
- [使用maven插件对java工程进行打包](http://chenzhou123520.iteye.com/blog/1706242)
