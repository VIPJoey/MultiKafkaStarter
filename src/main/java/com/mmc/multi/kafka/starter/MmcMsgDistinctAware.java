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
 * MmcMsgDistinctAware.
 *
 * @author VIPJoey
 * @since 2023/10/29 上午10:37
 */
public interface MmcMsgDistinctAware {

    /**
     * 代表kafka消息的唯一键，用于批次内分组.

     * @return 唯一键
     */
    String getRoutekey();

    /**
     * kafka消息生产或接收时间，用于批次内分组，根据时间去重，取最新的消息.
     *
     * @return 消息时间
     */
    Long getTimestamp();
}
