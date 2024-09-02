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

/**
 * MmcKafkaSender.
 *
 * @author VIPJoey
 * @since 2024/6/23 15:24
 */
public interface MmcKafkaSender {

    /**
     * 发送kafka消息.
     *
     * @param topic        topic名称
     * @param partitionKey 消息分区键
     * @param message      具体消息
     */
    void sendStringMessage(String topic, String partitionKey, String message);


    /**
     * 发送kafka消息.
     *
     * @param topic        topic名称
     * @param partitionKey 消息分区键
     * @param message      具体消息
     */
    void sendProtobufMessage(String topic, String partitionKey, byte[] message);
}
