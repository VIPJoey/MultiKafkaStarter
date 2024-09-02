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
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * MmcKafkaAbstractProcessor.
 *
 * @author VIPJoey
 * @date 2023/9/26 16:21
 */
@Slf4j
@Setter
public abstract class MmcKafkaAbstractProcessor<T> extends KafkaAbstractProcessor<T> implements BatchMessageListener<String, Object> {

    @Override
    public void onMessage(List<ConsumerRecord<String, Object>> records) {

        if (null == records || CollectionUtils.isEmpty(records)) {

            log.warn("{} records is null or records.value is empty.", name);
            return;
        }

        receiveMessage(records);
    }
}
