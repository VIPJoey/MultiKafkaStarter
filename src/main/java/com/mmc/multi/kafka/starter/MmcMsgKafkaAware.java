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
 * MmcMsgKafkaAware.
 *
 * @author VIPJoey
 * @date 2024/6/2 15:39
 */
public interface MmcMsgKafkaAware {

    /**
     * 注入topic.
     *
     * @param topic topic名称
     */
    void setTopic(String topic);

    /**
     * 注入offset.
     *
     * @param offset offset
     */
    void setOffset(long offset);
}
