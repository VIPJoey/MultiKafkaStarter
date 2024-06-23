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
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * MmcKafkaOutputContainer.
 *
 * @author VIPJoey
 * @date 2024/6/23 15:23
 */
@Getter
@Slf4j
public class MmcKafkaOutputContainer {

    /**
     * 存放所有生产者.
     */
    private final Map<String, MmcKafkaSender> outputs;

    /**
     * 构造函数.
     */
    public MmcKafkaOutputContainer(Map<String, MmcKafkaSender> outputs) {
        this.outputs = outputs;
    }

}
