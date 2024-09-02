/*
 * Copyright (c) 2010-2030 Founder Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Founder. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the agreements
 * you entered into with Founder.
 *
 */

package com.mmc.multi.kafka.starter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.UnsupportedForMessageFormatException;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * KafkaAbstractProcessor.
 *
 * @author VIPJoey
 * @date 2024/6/2 10:41
 */
@Slf4j
@Setter
public abstract class KafkaAbstractProcessor<T> implements MmcInputer {

    /**
     * Kafka容器.
     */
    protected ConcurrentMessageListenerContainer<Object, Object> container;
    /**
     * 消费者名称.
     */
    protected String name;
    /**
     * 消费者配置.
     */
    protected MmcMultiKafkaProperties.MmcKafkaProperties properties;
    /**
     * 当前处理器类型.
     */
    private Class<T> type = null;

    /**
     * KafkaAbstractProcessor.
     */
    public KafkaAbstractProcessor() {

    }

    /**
     * KafkaAbstractProcessor.
     *
     * @param name       处理器名称
     * @param properties 消费者配置
     */
    public KafkaAbstractProcessor(String name, MmcMultiKafkaProperties.MmcKafkaProperties properties) {

        this.name = name;
        this.properties = properties;
    }

    @Override
    public void init() {

    }

    /**
     * 消费kafka消息.
     */
    public void receiveMessage(List<ConsumerRecord<String, Object>> records) {

        if (null == records || CollectionUtils.isEmpty(records)) {

            log.warn("{} records is null or records.value is empty.", name);
            return;
        }

        Assert.hasText(name, "You must pass the field `name` to the Constructor or invoke the setName() after the class was created.");
        Assert.notNull(properties, "You must pass the field `properties` to the Constructor or invoke the setProperties() after the class was created.");

        try {

            Stream<T> dataStream = records.stream()
                    .flatMap(this::doParse)
                    .filter(Objects::nonNull)
                    .filter(this::isRightRecord);

            // 支持配置强制去重或实现了接口能力去重
            if (properties.isDuplicate() || isSubtypeOfInterface(MmcMsgDistinctAware.class)) {

                // 检查是否实现了去重接口
                if (!isSubtypeOfInterface(MmcMsgDistinctAware.class)) {
                    throw new RuntimeException("The interface "
                            + MmcMsgDistinctAware.class.getName() + " is not implemented if you set the config `spring.kafka.xxx.duplicate=true` .");
                }

                dataStream = dataStream.collect(Collectors.groupingBy(this::buildRoutekey))
                        .entrySet()
                        .stream()
                        .map(this::findLasted)
                        .filter(Objects::nonNull);
            }

            List<T> datas = dataStream.collect(Collectors.toList());
            if (CommonUtil.isNotEmpty(datas)) {

                this.dealMessage(datas);

            }


        } catch (Exception e) {

            log.error(name + "-dealMessage error ", e);
        }
    }

    protected boolean isSubtypeOfInterface(Class<?> interfaceClass) {

        if (null == type) {

            Type superClass = getClass().getGenericSuperclass();
            if (superClass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) superClass;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                    //noinspection unchecked
                    type = (Class<T>) typeArguments[0];
                }
            }
        }
        return (null != type) && interfaceClass.isAssignableFrom(type);
    }


    /**
     * 将kafka消息解析为实体，支持json对象或者json数组.
     *
     * @param map kafka消息对象，包含key、value、topic、partition、offset等
     * @return 实体类
     */
    protected Stream<T> doParse(ConsumerRecord<String, Object> map) {

        // 消息对象
        Object record = map.value();

        // 如果是pb格式
        if (record instanceof byte[]) {

            return doParseProtobuf((byte[]) record);

        } else if (record instanceof String) {

            // 普通kafka消息
            String json = record.toString();

            if (json.startsWith("[")) {

                // 数组
                List<T> datas = doParseJsonArray(json);
                if (CommonUtil.isEmpty(datas)) {

                    log.warn("{} doParse error, json={} is error.", name, json);
                    return Stream.empty();
                }

                // 反序列对象后，做一些初始化操作
                datas = datas.stream().peek(x -> doKafkaAware(x, map)).peek(this::doAfterParse).collect(Collectors.toList());

                return datas.stream();

            } else {

                // 对象
                T data = doParseJsonObject(json);
                if (null == data) {

                    log.warn("{} doParse error, json={} is error.", name, json);
                    return Stream.empty();
                }

                // 注入kafka相关
                doKafkaAware(data, map);

                // 反序列对象后，做一些初始化操作
                doAfterParse(data);

                return Stream.of(data);
            }

        } else if (record instanceof MmcKafkaMsg) {

            // 如果本身就是MmcKafkaMsg对象，直接返回
            //noinspection unchecked
            return Stream.of((T) record);

        } else {


            throw new UnsupportedForMessageFormatException("not support message type");
        }

    }

    protected void doKafkaAware(T x, ConsumerRecord<String, Object> record) {

        if (x instanceof MmcMsgKafkaAware) {
            ((MmcMsgKafkaAware) x).setOffset(record.offset());
            ((MmcMsgKafkaAware) x).setTopic(record.topic());
        }

    }

    /**
     * 将json消息解析为实体.
     *
     * @param json kafka消息
     * @return 实体类
     */
    protected T doParseJsonObject(String json) {
        if (properties.isSnakeCase()) {
            return JsonUtil.parseSnackJson(json, getEntityClass());
        } else {
            return JsonUtil.parseJsonObject(json, getEntityClass());
        }
    }

    /**
     * 将json消息解析为数组.
     *
     * @param json kafka消息
     * @return 数组
     */
    protected List<T> doParseJsonArray(String json) {
        if (properties.isSnakeCase()) {
            try {
                return JsonUtil.parseSnackJsonArray(json, getEntityClass());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return JsonUtil.parseJsonArray(json, getEntityClass());
        }
    }

    /**
     * 序列化为pb格式，假设你消费的是pb消息，需要自行实现这个类.
     *
     * @param record pb字节数组
     * @return pb实体类流
     */
    protected Stream<T> doParseProtobuf(byte[] record) {

        throw new NotImplementedException();
    }

    /**
     * 反序列对象后，做一些初始化操作.
     *
     * @param data 待处理的实体
     */
    protected void doAfterParse(T data) {

    }

    /**
     * 设置kafka容器.
     *
     * @param container kafka容器
     */
    public void setContainer(ConcurrentMessageListenerContainer<Object, Object> container) {

        this.container = container;
    }

    /**
     * 停止kafka容器.
     */
    public void stop() {
        container.stop();
    }

    /**
     * 启动kafka容器.
     */
    public void start() {
        container.start();
    }

    /**
     * 如果单次拉取的kafka消息有重复，则根据某个字段分组，并取最新的一个.
     *
     * @param entry entry
     * @return 最好更新的实体
     */
    protected T findLasted(Map.Entry<String, List<T>> entry) {

        try {

            Optional<T> d = entry.getValue().stream()
                    .max(Comparator.comparing(x -> ((MmcMsgDistinctAware) x).getRoutekey()));

            if (d.isPresent()) {

                return d.get();
            }

        } catch (Exception e) {

            String content = JsonUtil.toJsonStr(entry.getValue());
            log.error("处理消息出错:{}", e.getMessage() + ": " + content, e);
        }
        return null;
    }

    /**
     * 构造实体类的唯一键.
     *
     * @param t 待处理实体
     * @return 实体类的唯一键
     */
    protected String buildRoutekey(T t) {
        return ((MmcMsgDistinctAware) t).getRoutekey();
    }


    /**
     * 过滤消息.
     *
     * @param t 待处理实体
     * @return true:不过滤，false:过滤
     */
    protected boolean isRightRecord(T t) {
        return true;
    }

    /**
     * 获取反序列的实体类类型.
     *
     * @return 反序列的实体类类型
     */
    protected Class<T> getEntityClass() {


        if (null == type) {

            synchronized (this) {

                Type superClass = getClass().getGenericSuperclass();
                if (superClass instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) superClass;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                        //noinspection unchecked
                        type = (Class<T>) typeArguments[0];
                    }
                }
            }

        }

        return type;
    }

    /**
     * 处理消息.
     *
     * @param datas 待处理列表
     */
    protected abstract void dealMessage(List<T> datas) throws ExecutionException, InterruptedException;

}
