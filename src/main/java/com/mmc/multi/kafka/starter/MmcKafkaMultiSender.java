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

import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MmcKafkaMultiSender.
 *
 * @author tenkye
 * @date 2024/6/23 16:18
 */
public class MmcKafkaMultiSender implements MmcKafkaSender {

    private final AtomicLong atomicLong = new AtomicLong(1);

    private final List<KafkaTemplate<String, Object>> templates;

    public MmcKafkaMultiSender(List<KafkaTemplate<String, Object>> templates) {
        this.templates = templates;
    }

    @Override
    public void sendStringMessage(String topic, String partitionKey, String message) {

        KafkaTemplate<String, Object> template = templates.get((int) (atomicLong.getAndIncrement() % templates.size()));
        template.send(topic, partitionKey, message);
    }


    @Override
    public void sendProtobufMessage(String topic, String partitionKey, byte[] message) {

        KafkaTemplate<String, Object> template = templates.get((int) (atomicLong.getAndIncrement() % templates.size()));
        template.send(topic, partitionKey, message);

    }
}
