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

/**
 * MmcKafkaSingleSender.
 *
 * @author VIPJoey
 * @since 2024/6/23 15:28
 */
public class MmcKafkaSingleSender implements MmcKafkaSender {

    private final KafkaTemplate<String, Object> template;


    public MmcKafkaSingleSender(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    @Override
    public void sendStringMessage(String topic, String partitionKey, String message) {

        template.send(topic, partitionKey, message);
    }


    @Override
    public void sendProtobufMessage(String topic, String partitionKey, byte[] message) {

        template.send(topic, partitionKey, message);

    }

}
