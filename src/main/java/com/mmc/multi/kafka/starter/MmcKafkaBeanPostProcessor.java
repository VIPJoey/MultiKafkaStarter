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

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MmcKafkaBeanPostProcessor.
 *
 * @author VIPJoey
 * @date 2023/10/29 下午5:52
 */
public class MmcKafkaBeanPostProcessor implements BeanPostProcessor {

    @Getter
    private final Map<String, KafkaAbstractProcessor<?>> suitableClass = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof KafkaAbstractProcessor) {

            KafkaAbstractProcessor<?> target = (KafkaAbstractProcessor<?>) bean;
            suitableClass.putIfAbsent(beanName, target);
            suitableClass.putIfAbsent(bean.getClass().getName(), target);
        }

        return bean;
    }
}
