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

import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

/**
 * MmcInputer.
 *
 * @author VIPJoey
 * @date 2023/10/15 下午4:31
 */
public interface MmcInputer {


    /**
     * 设置kafka容器.
     *
     * @param container kafka容器
     */
    void setContainer(ConcurrentMessageListenerContainer<Object, Object> container);

    /**
     * 停止kafka容器.
     */
    void stop();

    /**
     * 启动kafka容器.
     */
    void start();

    /**
     * 初始化kafka容器.
     */
    void init();
}
