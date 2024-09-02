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

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * com.mmc.multi.kafka.starter.FiveProcessor.
 *
 * @author VIPJoey
 * @date 2024/9/1 20:59
 */
@Slf4j
@Service("fiveProcessor")
public class FiveProcessor extends MmcKafkaParallelAbstractProcessor<ParalleMsg, Void> {


    @Override
    protected Void handelBatchDatas(List<ParalleMsg> datas) {
        datas.forEach(x -> {
            log.info("handelBatchDatas one: {}", x);
        });

        return null;
    }
}
