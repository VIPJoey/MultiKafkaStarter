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
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MmcKafkaKafkaAbastrctProcessor.
 *
 * @author VIPJoey
 * @date 2023/9/26 16:21
 */
@Slf4j
@Setter
public abstract class MmcKafkaKafkaAbastrctProcessor<T extends MmcKafkaMsg> implements MmcKafkaStringInputer {

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

    public MmcKafkaKafkaAbastrctProcessor() {

    }

    public MmcKafkaKafkaAbastrctProcessor(String name, MmcMultiKafkaProperties.MmcKafkaProperties properties) {

        this.name = name;
        this.properties = properties;
    }

    /**
     * 消费kafka消息.
     */
    @Override
    public void onMessage(List<ConsumerRecord<String, String>> records) {

        if (null == records || CollectionUtils.isEmpty(records)) {

            log.warn("{} records is null or records.value is empty.", name);
            return;
        }

        Assert.hasText(name, "You must pass the field `name` to the Constructor or invoke the setName() after the class was created.");
        Assert.notNull(properties, "You must pass the field `properties` to the Constructor or invoke the setProperties() after the class was created.");

        try {

            Stream<T> dataStream = records.stream()
                    .map(ConsumerRecord::value)
                    .flatMap(this::doParse)
                    .filter(Objects::nonNull)
                    .filter(this::isRightRecord);

            if (properties.isDuplicate()) {

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


    /**
     * 将kafka消息解析为实体，支持json对象或者json数组.
     *
     * @param json kafka消息
     * @return 实体类
     */
    protected Stream<T> doParse(String json) {

        if (json.startsWith("[")) {

            // 数组
            List<T> datas = JsonUtil.parseJsonArray(json, getEntityClass());
            if (CommonUtil.isEmpty(datas)) {

                log.warn("{} doParse error, json={} is error.", name, json);
                return Stream.empty();
            }

            // 反序列对象后，做一些初始化操作
            datas = datas.stream().peek(this::doAfterParse).collect(Collectors.toList());

            return datas.stream();

        } else {

            // 对象
            T data = JsonUtil.parseJsonObject(json, getEntityClass());
            if (null == data) {

                log.warn("{} doParse error, json={} is error.", name, json);
                return Stream.empty();
            }

            // 反序列对象后，做一些初始化操作
            doAfterParse(data);

            return Stream.of(data);
        }
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
    protected  T findLasted(Map.Entry<String, List<T>> entry) {

        try {

            Optional<T> d = entry.getValue().stream()
                    .max(Comparator.comparing(T::getRoutekey));

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
        return t.getRoutekey();
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
    protected abstract Class<T> getEntityClass();

    /**
     * 处理消息.
     *
     * @param datas 待处理列表
     */
    protected abstract void dealMessage(List<T> datas) throws ExecutionException, InterruptedException;


}
