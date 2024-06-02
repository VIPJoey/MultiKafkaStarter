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
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DemoService.
 *
 * @author VIPJoey
 * @date 2023/10/29 下午5:29
 */
@Slf4j
@Service
public class DemoService {

    public void dealMessage(String name, List<MmcMsgDistinctAware> datas) {

        for (MmcMsgDistinctAware msg : datas) {

            log.info(name + " receive: " + JsonUtil.toJsonStr(msg));

        }

    }
}
