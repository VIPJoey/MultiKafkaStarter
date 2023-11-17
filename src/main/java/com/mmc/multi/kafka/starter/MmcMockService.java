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

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * MmcMockService.
 *
 * @author VIPJoey
 * @date 2023/10/29 下午4:41
 */
@Slf4j
public class MmcMockService {

    public void dealMessage(List<MmcKafkaMsg> datas) {

        for (MmcKafkaMsg msg : datas) {

            log.info(JsonUtil.toJsonStr(msg));

        }

    }
}
