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

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.util.StringUtils;

/**
 * BaseConsumerConfiguration.
 *
 * @author VIPJoey
 * @date 2023/10/15 下午3:52
 */
class BaseConsumerConfiguration {


    /**
     * 消费者工厂.
     */
    protected ConcurrentKafkaListenerContainerFactory<Object, Object> concurrentKafkaListenerContainerFactory(
            MmcMultiKafkaProperties.MmcKafkaProperties properties) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> container =
                new ConcurrentKafkaListenerContainerFactory<>();

        // 覆盖里层的groupId
        if (StringUtils.hasText(properties.getGroupId())) {
            properties.getConsumer().setGroupId(properties.getGroupId());
        }

        container.setConsumerFactory(
                new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties()));
        // 设置并发量，小于或等于Topic的分区数
        container.setConcurrency(properties.getConcurrency());
        // 设置为批量监听
        container.setBatchListener("batch".equalsIgnoreCase(properties.getType()));

        return container;
    }

    /**
     * 监听器容器.
     */
    protected ConcurrentMessageListenerContainer<Object, Object> concurrentMessageListenerContainer(
            MmcMultiKafkaProperties.MmcKafkaProperties properties) {
        // 输入源配置
        return concurrentKafkaListenerContainerFactory(properties).createContainer(properties.getTopic().split(","));
    }
}
