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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * MmcMultiKafkaProperties.
 *
 * @author VIPJoey
 * @since 2023/10/14 下午4:44
 */
@ToString
@Data
@ConfigurationProperties(prefix = "spring")
public class MmcMultiKafkaProperties {

    /**
     * 支持多个kafka配置.
     */
    private Map<String, MmcKafkaProperties> kafka = new HashMap<>();


    /**
     * MmcKafkaProperties.
     */
    @Data
    static class MmcKafkaProperties {

        /**
         * 是否启用.
         */
        private boolean enabled;
        /**
         * 主题(支持配置多个topic，英文逗号分隔).
         */
        private String topic;
        /**
         * 消费组.
         */
        private String groupId;
        /**
         * 并发度.
         */
        private Integer concurrency = 1;
        /**
         * 批量消费.
         */
        private String type = "batch";
        /**
         * 是否在批次内对kafka进行去重，默认为false.
         */
        private boolean duplicate = false;
        /**
         * json是否为下划线模式，默认为false.
         */
        private boolean snakeCase = false;
        /**
         * 处理类.
         */
        private String processor;
        /**
         * 消费者.
         */
        private final KafkaProperties.Consumer consumer = new KafkaProperties.Consumer();
        /**
         * 生产者.
         */
        private final Producer producer = new Producer();
        /**
         * 监听器.
         */
        private final KafkaProperties.Listener listener = new KafkaProperties.Listener();
        /**
         * 并发设置.
         */
        private Container container = new Container();

        /**
         * Create an initial map of consumer properties from the state of this instance.
         * <p>
         * This allows you to add additional properties, if necessary, and override the
         * default kafkaConsumerFactory bean.
         *
         * @return the consumer properties initialized with the customizations defined on this
         *         instance
         */
        public Map<String, Object> buildConsumerProperties() {
            return new HashMap<>(this.consumer.buildProperties());
        }

        /**
         * Create an initial map of producer properties from the state of this instance.
         * <p>
         * This allows you to add additional properties, if necessary, and override the
         * default kafkaProducerFactory bean.
         *
         * @return the producer properties initialized with the customizations defined on this
         *         instance
         */
        Map<String, Object> buildProducerProperties() {
            return new HashMap<>(this.producer.buildProperties());
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Producer extends KafkaProperties.Producer {

        /**
         * 是否启用.
         */
        private boolean enabled = true;

        /**
         * 生产者数量.
         */
        private int count = 1;

        /**
         * 生产者名称，如果有设置则会覆盖默认的xxxKakfkaSender名称.
         */
        private String name;
    }

    @Data
    public static class Container implements ContainerConfig {

        /**
         * 是否启用多线程消费.
         */
        private boolean enabled = true;
        /*
         * 消费消息的速率（每秒接收的记录数），默认值为1000.
         */
        private int rate = 1000;
        /*
         * 最小批次数量，默认为2.
         */
        private int threshold = 2;
        /*
         * 设置并行度，默认值为可用处理器数量.
         */
        private int parallelism = Runtime.getRuntime().availableProcessors();
    }
}
