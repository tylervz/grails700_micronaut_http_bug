## Grails 7.0.0 Micronaut Http Client bug

This sample Grails 7.0.0 application was created to demonstrate an issue with dependency constraints in Grails 7.0.0 applications with the Micronaut HTTP Client feature.

This Grails 7.0.0 application was created from [Grails Forge](https://start.grails.org/) with the following settings:
- Java 17
- Gorm Hibernate5
- Embedded Servlet Container: Spring Boot Starter Tomcat
- Test Framework: Spock
- Additional Selected Features: Micronaut HTTP Client

## The Issue

The `HttpClient.create()` line in [`HelloControllerIntegrationSpec.groovy`](src/integration-test/groovy/com/example/HelloControllerIntegrationSpec.groovy) is throwing an exception:
```
String baseUrl = "http://localhost:$serverPort"
this.client  = HttpClient.create(baseUrl.toURL())
```
The exception is:
```
java.lang.NoClassDefFoundError: io/netty/channel/MultiThreadIoEventLoopGroup
    at io.micronaut.http.client.netty.ConnectionManager.createEventLoopGroup(ConnectionManager.java:277)
    at io.micronaut.http.client.netty.ConnectionManager.<init>(ConnectionManager.java:234)
    at io.micronaut.http.client.netty.DefaultHttpClient.<init>(DefaultHttpClient.java:424)
    at io.micronaut.http.client.netty.DefaultHttpClientBuilder.build(DefaultHttpClientBuilder.java:283)
    at io.micronaut.http.client.netty.NettyHttpClientFactory.createNettyClient(NettyHttpClientFactory.java:141)
    at io.micronaut.http.client.netty.NettyHttpClientFactory.createNettyClient(NettyHttpClientFactory.java:126)
    at io.micronaut.http.client.netty.NettyHttpClientFactory.createClient(NettyHttpClientFactory.java:57)
    at io.micronaut.http.client.HttpClient.create(HttpClient.java:251)
    at com.example.HelloControllerIntegrationSpec.$tt__init(HelloControllerIntegrationSpec.groovy:24)
    at com.example.HelloControllerIntegrationSpec.init_closure1(HelloControllerIntegrationSpec.groovy)
    at groovy.lang.Closure.call(Closure.java:433)
    at groovy.lang.Closure.call(Closure.java:422)
    at grails.gorm.transactions.GrailsTransactionTemplate$2.doInTransaction(GrailsTransactionTemplate.groovy:98)
    at org.springframework.transaction.support.TransactionTemplate.execute(TransactionTemplate.java:140)
    at grails.gorm.transactions.GrailsTransactionTemplate.execute(GrailsTransactionTemplate.groovy:95)
    at org.spockframework.util.ReflectionUtil.invokeMethod(ReflectionUtil.java:187)
    at org.spockframework.runtime.model.MethodInfo.lambda$new$0(MethodInfo.java:49)
    at org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
    at org.spockframework.runtime.extension.MethodInvocation.proceed(MethodInvocation.java:102)
    at org.grails.testing.spock.RunOnceInterceptor.interceptSetupMethod(RunOnceInterceptor.groovy:36)
    at org.spockframework.runtime.extension.AbstractMethodInterceptor.intercept(AbstractMethodInterceptor.java:30)
    at org.spockframework.runtime.extension.MethodInvocation.proceed(MethodInvocation.java:101)
    at org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
    at org.spockframework.runtime.extension.MethodInvocation.proceed(MethodInvocation.java:102)
    at org.spockframework.spring.SpringInterceptor.interceptSetupMethod(SpringInterceptor.java:55)
    at org.spockframework.runtime.extension.AbstractMethodInterceptor.intercept(AbstractMethodInterceptor.java:30)
    at org.spockframework.runtime.extension.MethodInvocation.proceed(MethodInvocation.java:101)
    at org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
    at org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
    at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
    at org.spockframework.runtime.model.MethodInfo.invoke(MethodInfo.java:156)
    at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
Caused by: java.lang.ClassNotFoundException: io.netty.channel.MultiThreadIoEventLoopGroup
    at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
    at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
    at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
    ... 32 more
```

Some researching revealed that the `io.netty.channel.MultiThreadIoEventLoopGroup` class was introduced in Netty 4.2.x.

Micronaut platform version (4.9.2) expects Netty 4.2.x, but Spring Boot 3.5.6 (from Grails BOM) forces Netty 4.1.x.

## Steps To Reproduce

Clone this repository and run the integration tests on the `master` branch.

```shell
./gradlew integrationTest
```

## Fixes

I was able to get the test passing on the `fix` branch of this repository by downgrading the Micronaut platform version, specified in `gradle.properties`, to `4.7.6` which is compatible with Netty 4.1.x.
Micronaut platforms 4.8+ moved to Netty 4.2.x.

Following [the advice of @matrei](https://github.com/apache/grails-core/issues/15149#issuecomment-3415944420),
I was able to get the test passing on the `fix2` branch of this repository using a different approach of using the `micronaut-http-client-jdk` dependency instead of the `micronaut-http-client` dependency.

## Environment Information

OS: Mac OS 15.6.1
JDK: 17.0.15 Temurin
