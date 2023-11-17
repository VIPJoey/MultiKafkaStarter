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

import org.springframework.kafka.listener.BatchMessageListener;

/**
 * MmcStringInputer.
 *
 * @author VIPJoey
 * @date 2023/10/15 下午4:42
 */
public interface MmcKafkaStringInputer extends MmcInputer, BatchMessageListener<String, String> {

}
