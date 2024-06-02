# MultiKafkaConsumerStarter [V2.1]
SpringBoot 零代码方式整合多个kafka数据源，支持任意kafka集群，已封装为一个小模块，集成所有kafka配置，让注意力重新回归业务本身。

## 一、功能特性

* SpringBoot无编程方式整合多个kafka数据源
* 支持批量消费kafka并对单批次消息根据条件去重
* 支持消费kafka消息类型为pb格式

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

## protobuf类型的消息的kafka配置
spring.kafka.pb.enabled=true
spring.kafka.pb.consumer.bootstrapServers=${spring.embedded.kafka.brokers}
spring.kafka.pb.topic=mmc-topic-pb
spring.kafka.pb.group-id=group-consumer-pb
spring.kafka.pb.processor=pbProcessor
spring.kafka.pb.consumer.auto-offset-reset=latest
spring.kafka.pb.consumer.max-poll-records=10
spring.kafka.pb.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.pb.consumer.value-deserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer


```

3、新建kafka消息对应的实体类，可以选择实现`MmcMsgDistinctAware`接口，例如
```java
@Data
class DemoMsg implements MmcMsgDistinctAware {

    private String routekey;

    private String name;

    private Long timestamp;

}

如果你配置了spring.kafka.xxx.duplicate=fale，则不需要实现MmcMsgDistinctAware接口。

        PS：如果实现MmcMsgDistinctAware接口，就自动具备了消息去重能力


```

4、新建kafka消息处理类，要求继承`MmcKafkaKafkaAbastrctProcessor`，然后就可以愉快地编写你的业务逻辑了。
```java
@Slf4j
@Service("oneProcessor") // 你的处理类bean名称，如果没有定义bean名称，那么默认就是首字母缩写的类名称
public class OneProcessor extends MmcKafkaKafkaAbastrctProcessor<DemoMsg> {
    

    @Override
    protected void dealMessage(List<DemoMsg> datas) {

        // 下面开始编写你的业务代码
    }


}

@Slf4j
@Service("pbProcessor")
public class PbProcessor extends MmcKafkaKafkaAbastrctProcessor<DemoMsg> {

    @Override
    protected Stream<DemoMsg> doParseProtobuf(byte[] record) {


        try {

            DemoPb.PbMsg msg = DemoPb.PbMsg.parseFrom(record);
            DemoMsg demo = new DemoMsg();
            BeanUtils.copyProperties(msg, demo);

            return Stream.of(demo);

        } catch (InvalidProtocolBufferException e) {

            log.error("parssPbError", e);
            return Stream.empty();
        }

    }

    @Override
    protected void dealMessage(List<DemoMsg> datas) {

        System.out.println("PBdatas: " + datas);

    }
}


```

## 三、其它特性

1、支持单次拉取kafka的batch消息里去重，需要实现`PandoKafkaMsg`的getRoutekey()和getTimestamp()方法；如果为false，则不要实现`PandoKafkaMsg`接口。
```properties
spring.kafka.xxx.duplicate=true
```

2、支持字符串kafka消息，json是驼峰或者下划线
```properties
# 默认为支持驼峰的kafka消息，为ture则支持下划线的消息
spring.kafka.xxx.snakeCase=false
```


3、支持pb的kafka消息，需要自行重写父类的`doParseProtobuf`方法；
```java
    @Override
    protected Stream<DemoMsg> doParseProtobuf(byte[] record) {
    
            try {
    
                DemoMsg msg = new DemoMsg();
                DemoPb.PbMsg pb = DemoPb.PbMsg.parseFrom(record);
                BeanUtils.copyProperties(pb, msg);
        
                return Stream.of(msg);
        
                } catch (InvalidProtocolBufferException e) {
        
                log.error("doParseProtobuf error: {}", e.getMessage());
        
                return Stream.empty();
            }

        }
```

4、支持获取kafka的topic、offset属性，注入到实体类中，需要实现`MmcMsgKafkaAware`接口
```java
@Data
class DemoAwareMsg implements PandoKafkaAware {
    
    private String routekey;

    private String name;

    private Long timestamp;

    private String topic;

    private long offset;

}


```

## 四、变更记录

* 20240602  v2.1 支持获取kafka消息中topic、offset属性
* 20240602  v2.0 支持protobuf、json格式
* 20240430  v1.1 取消限定符
* 20231111  v1.0 初始化

## 五、参考文章

* [《搭建大型分布式服务（三十六）SpringBoot 零代码方式整合多个kafka数据源》](https://blog.csdn.net/hanyi_/article/details/133826712?spm=1001.2014.3001.5502)
* [《搭建大型分布式服务（三十七）SpringBoot 整合多个kafka数据源-取消限定符》](https://blog.csdn.net/hanyi_/article/details/135940206)
* [《搭建大型分布式服务（三十八）SpringBoot 整合多个kafka数据源-支持protobuf》](https://blog.csdn.net/hanyi_/article/details/139387941?spm=1001.2014.3001.5502)
* [《搭建大型分布式服务（三十九）SpringBoot 整合多个kafka数据源-支持Aware模式》](https://blog.csdn.net/hanyi_/article/details/139392161?spm=1001.2014.3001.5502)


## 六、特别说明

* 欢迎共建
* 佛系改bug