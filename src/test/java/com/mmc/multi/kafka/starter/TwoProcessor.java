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

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DemoProcessor.
 *
 * @author VIPJoey
 * @date 2023/10/29 上午10:26
 */
@Slf4j
@Service
public class TwoProcessor extends MmcKafkaKafkaAbastrctProcessor<DemoMsg> {

    @Resource
    private DemoService demoService;

    public TwoProcessor() {


    }

    @Override
    protected Class<DemoMsg> getEntityClass() {
        return DemoMsg.class;
    }

    @Override
    protected void dealMessage(List<DemoMsg> datas) {


        demoService.dealMessage("two", datas.stream().map(x -> (MmcKafkaMsg) x).collect(Collectors.toList()));

    }


}
