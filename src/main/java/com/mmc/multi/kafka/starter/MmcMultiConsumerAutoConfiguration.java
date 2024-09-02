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

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * MmcMultiConsumerAutoConfiguration.
 *
 * @author VIPJoey
 * @date 2023/10/14 下午4:28
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MmcMultiKafkaProperties.class)
@ConditionalOnProperty(prefix = "spring.kafka", value = "enabled", matchIfMissing = true)
public class MmcMultiConsumerAutoConfiguration extends BaseConsumerConfiguration {

    @Resource
    private MmcMultiKafkaProperties mmcMultiKafkaProperties;

    @Bean
    public MmcKafkaBeanPostProcessor mmcKafkaBeanPostProcessor() {
        return new MmcKafkaBeanPostProcessor();
    }

    @Bean
    public MmcKafkaProcessorFactory processorFactory() {

        return new MmcKafkaProcessorFactory();
    }

    @Bean
    public MmcKafkaInputerContainer mmcKafkaInputerContainer(MmcKafkaProcessorFactory factory,
                                                             MmcKafkaBeanPostProcessor beanPostProcessor) throws Exception {

        Map<String, MmcInputer> inputers = new HashMap<>();

        Map<String, MmcMultiKafkaProperties.MmcKafkaProperties> kafkas = mmcMultiKafkaProperties.getKafka();

        // 逐个遍历，并生成consumer
        for (Map.Entry<String, MmcMultiKafkaProperties.MmcKafkaProperties> entry : kafkas.entrySet()) {

            // 唯一消费者名称
            String name = entry.getKey();

            // 消费者配置
            MmcMultiKafkaProperties.MmcKafkaProperties properties = entry.getValue();

            // 是否开启
            if (properties.isEnabled() && CommonUtil.isNotBlank(properties.getGroupId())) {

                // 生成消费者
                KafkaAbstractProcessor<?> inputer = factory.buildInputer(name, properties, beanPostProcessor.getSuitableClass());

                // 输入源容器
                ConcurrentMessageListenerContainer<Object, Object> container = concurrentMessageListenerContainer(properties);

                // 设置容器
                inputer.setContainer(container);
                inputer.setName(name);
                inputer.setProperties(properties);
                inputer.init();

                // 设置消费者
                container.setupMessageListener(inputer);

                // 关闭时候停止消费
                Runtime.getRuntime().addShutdownHook(new Thread(inputer::stop));

                // 直接启动
                container.start();

                // 加入集合
                inputers.put(name, inputer);
            }

        }

        return new MmcKafkaInputerContainer(inputers);
    }




}
