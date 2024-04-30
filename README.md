# MultiKafkaConsumerStarter [V1.1]
SpringBoot 零代码方式整合多个kafka数据源，支持任意kafka集群，已封装为一个小模块，集成所有kafka配置，让注意力重新回归业务本身。

[参考文档](http://t.csdnimg.cn/SHwBF)

## 一、功能特性

* SpringBoot无编程方式整合多个kafka数据源

## 二、快速开始

1、引入最新依赖包，如果找不到依赖包，请到工程目录```mvn clean package install```执行一下命令。
```xml
<dependency>
    <groupId>io.github.vipjoey</groupId>
    <artifactId>multi-kafka-consumer-starter</artifactId>
    <version>最新版本号</version>
</dependency>

```

2、添加kafka地址等相关配置。
```properties
## topic1的kafka配置
spring.kafka.one.enabled=true
spring.kafka.one.consumer.bootstrapServers=${spring.embedded.kafka.brokers}
spring.kafka.one.topic=mmc-topic-one
spring.kafka.one.group-id=group-consumer-one
spring.kafka.one.processor=你的处理类bean名称（例如：oneProcessor）
spring.kafka.one.dupicate=true   ## 如果为true表示对批次内的kafka消息去重，需要实现PandoKafkaMsg接口，默认为false
spring.kafka.one.consumer.auto-offset-reset=latest
spring.kafka.one.consumer.max-poll-records=10
spring.kafka.one.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.one.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer

## topic2的kafka配置
spring.kafka.two.enabled=true
spring.kafka.two.consumer.bootstrapServers=${spring.embedded.kafka.brokers}
spring.kafka.two.topic=mmc-topic-two
spring.kafka.two.group-id=group-consumer-two
spring.kafka.two.processor=你的处理类bean名称
spring.kafka.two.consumer.auto-offset-reset=latest
spring.kafka.two.consumer.max-poll-records=10
spring.kafka.two.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.two.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer


```

3、新建kafka消息对应的实体类，要求需要实现`MmcKafkaMsg`接口，例如
```java
@Data
class DemoMsg implements MmcKafkaMsg {

    private String routekey;

    private String name;

    private Long timestamp;

}

如果你配置了spring.kafka.xxx.duplicate=fale，则不需要实现PandoKafkaMsg接口。

        PS：如果实现PandoKafkaMsg接口，就自动具备了消息去重能力


```

4、新建kafka消息处理类，要求继承`MmcKafkaKafkaAbastrctProcessor`，然后就可以愉快地编写你的业务逻辑了。
```java
@Slf4j
@Service("oneProcessor") // 你的处理类bean名称，如果没有定义bean名称，那么默认就是首字母缩写的类名称
public class OneProcessor extends MmcKafkaKafkaAbastrctProcessor<DemoMsg> {


    @Override
    protected Class<DemoMsg> getEntityClass() {
        return DemoMsg.class;
    }

    @Override
    protected void dealMessage(List<DemoMsg> datas) {

        // 下面开始编写你的业务代码
    }


}
```

## 三、其它特性

1、支持单次拉取kafka的batch消息里去重，需要实现`PandoKafkaMsg`的getRoutekey()和getTimestamp()方法；如果为false，则不要实现`PandoKafkaMsg`接口。
```properties
spring.kafka.xxx.duplicate=true
```

## 四、变更记录

* 20240430  v1.1 取消限定符
* 20231111  v1.0 初始化


## 五、特别说明

* 欢迎共建
* 佛系改bug