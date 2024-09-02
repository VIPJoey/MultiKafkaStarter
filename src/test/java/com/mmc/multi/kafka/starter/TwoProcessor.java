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

/**
 * DemoProcessor.
 *
 * @author VIPJoey
 * @since 2023/10/29 上午10:26
 */
@Slf4j
@Service
public class TwoProcessor extends MmcKafkaAbstractProcessor<DemoMsg> {

    @Resource
    private DemoService demoService;

    public TwoProcessor() {


    }

    @Override
    protected void dealMessage(List<DemoMsg> datas) {


        datas.forEach(x -> {
            log.info("dealMessage two: {}", x);
        });

    }


}
