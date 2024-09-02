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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.Resource;
import java.util.*;

/**
 * MmcMultiProducerAutoConfiguration.
 *
 * @author VIPJoey
 * @since 2024/6/23 15:09
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MmcMultiKafkaProperties.class)
@ConditionalOnProperty(prefix = "spring.kafka", value = "enabled", matchIfMissing = true)
public class MmcMultiProducerAutoConfiguration implements BeanFactoryAware {

    private DefaultListableBeanFactory beanDefinitionRegistry;

    @Resource
    private MmcMultiKafkaProperties mmcMultiKafkaProperties;


    @Bean
    public MmcKafkaOutputContainer mmcKafkaOutputContainer() {

        // 初始化一个存储所有生产者的哈希映射
        Map<String, MmcKafkaSender> outputs = new HashMap<>();

        // 获取所有的Kafka配置信息
        Map<String, MmcMultiKafkaProperties.MmcKafkaProperties> kafkas = mmcMultiKafkaProperties.getKafka();

        // 逐个遍历，并生成producer
        for (Map.Entry<String, MmcMultiKafkaProperties.MmcKafkaProperties> entry : kafkas.entrySet()) {

            // 唯一生产者名称
            String name = entry.getKey();

            // 生产者配置
            MmcMultiKafkaProperties.MmcKafkaProperties properties = entry.getValue();

            // 是否开启
            if (properties.isEnabled()
                    && properties.getProducer().isEnabled()
                    && CommonUtil.isNotEmpty(properties.getProducer().getBootstrapServers())) {

                // bean名称
                String beanName = Optional.ofNullable(properties.getProducer().getName())
                        .orElse(name + "KafkaSender");


                // 数量
                List<KafkaTemplate<String, Object>> templates = new ArrayList<>(properties.getProducer().getCount());
                for (int i = 0; i < properties.getProducer().getCount(); i++) {

                    log.info("[pando] init producer {} - {} ", name, i);
                    KafkaTemplate<String, Object> template = mmcKafkaTemplate(properties);
                    templates.add(template);
                }

                // 创建实例
                MmcKafkaSender sender = new MmcKafkaMultiSender(templates);
                outputs.put(beanName, sender);

                // 注册到IOC
                beanDefinitionRegistry.registerSingleton(beanName, sender);
            }

        }

        return new MmcKafkaOutputContainer(outputs);
    }

    private KafkaTemplate<String, Object> mmcKafkaTemplate(MmcMultiKafkaProperties.MmcKafkaProperties producer) {

        return new KafkaTemplate<>(baseKafkaProducerFactory(producer));

    }

    private ProducerFactory<String, Object> baseKafkaProducerFactory(MmcMultiKafkaProperties.MmcKafkaProperties producer) {
        return new DefaultKafkaProducerFactory<>(producer.buildProducerProperties());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanDefinitionRegistry = (DefaultListableBeanFactory) beanFactory;
    }
}
