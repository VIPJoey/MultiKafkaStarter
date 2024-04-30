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

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * MmcKafkaProcessorFactory.
 *
 * @author VIPJoey
 * @date 2023/10/29 下午12:51
 */
public class MmcKafkaProcessorFactory {

    @Resource
    private DefaultListableBeanFactory defaultListableBeanFactory;

    public MmcKafkaKafkaAbastrctProcessor<? > buildInputer(
            String name, MmcMultiKafkaProperties.MmcKafkaProperties properties,
            Map<String, MmcKafkaKafkaAbastrctProcessor<? >> suitableClass) throws Exception {

        // 如果没有配置process，则直接从注册的Bean里查找
        if (!StringUtils.hasText(properties.getProcessor())) {

            return findProcessorByName(name, properties.getProcessor(), suitableClass);
        }

        // 如果配置了process，则从指定配置中生成实例
        // 判断给定的配置是类，还是bean名称
        if (!isClassName(properties.getProcessor())) {

            throw new IllegalArgumentException("It's not a class, wrong value of ${spring.kafka." + name + ".processor}.");
        }

        // 如果ioc容器已经存在该处理实例，则直接使用，避免既配置了process，又使用了@Service等注解
        MmcKafkaKafkaAbastrctProcessor<? > inc = findProcessorByClass(name, properties.getProcessor(), suitableClass);
        if (null != inc) {
            return inc;
        }

        // 指定的processor处理类必须继承MmcKafkaKafkaAbastrctProcessor
        Class<?> clazz = Class.forName(properties.getProcessor());
        boolean isSubclass = MmcKafkaKafkaAbastrctProcessor.class.isAssignableFrom(clazz);
        if (!isSubclass) {
            throw new IllegalStateException(clazz.getName() + " is not subClass of MmcKafkaKafkaAbastrctProcessor.");
        }

        // 创建实例
        Constructor<?> constructor = clazz.getConstructor();
        MmcKafkaKafkaAbastrctProcessor<? > ins = (MmcKafkaKafkaAbastrctProcessor<? >) constructor.newInstance();

        // 注入依赖的变量
        defaultListableBeanFactory.autowireBean(ins);

        return ins;
    }

    private MmcKafkaKafkaAbastrctProcessor<? > findProcessorByName(String name, String processor, Map<String,
            MmcKafkaKafkaAbastrctProcessor<? >> suitableClass) {

        return suitableClass.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(name) || e.getKey().equalsIgnoreCase(processor))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't found any suitable processor class for the consumer which name is " + name
                        + ", please use the config ${spring.kafka." + name + ".processor} or set name of Bean like @Service(\"" + name + "Processor\") "));
    }


    private MmcKafkaKafkaAbastrctProcessor<? > findProcessorByClass(String name, String processor, Map<String,
            MmcKafkaKafkaAbastrctProcessor<? >> suitableClass) {

        return suitableClass.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(name) || e.getKey().equalsIgnoreCase(processor))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private boolean isClassName(String processor) {

        // 使用正则表达式验证类名格式
        String regex = "^[a-zA-Z_$][a-zA-Z\\d_$]*([.][a-zA-Z_$][a-zA-Z\\d_$]*)*$";
        return Pattern.matches(regex, processor);
    }

}
