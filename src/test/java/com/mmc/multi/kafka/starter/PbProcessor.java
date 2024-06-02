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

import com.google.protobuf.InvalidProtocolBufferException;
import com.mmc.multi.kafka.starter.proto.DemoPb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * PbProcessor.
 *
 * @author VIPJoey
 * @date 2024/6/2 11:15
 */
@Slf4j
@Service("pbProcessor")
public class PbProcessor extends MmcKafkaKafkaAbastrctProcessor<DemoMsg> {

    @Override
    protected Stream<DemoMsg> doParseProtobuf(byte[] record) {


        try {

            DemoPb.PbMsg msg = DemoPb.PbMsg.parseFrom(record);
            DemoMsg demo = new DemoMsg();
            BeanUtils.copyProperties(msg, demo);

            return Stream.of(demo);

        } catch (InvalidProtocolBufferException e) {

            log.error("parssPbError", e);
            return Stream.empty();
        }

    }

    @Override
    protected void dealMessage(List<DemoMsg> datas) {

        System.out.println("PBdatas: " + datas);

    }
}
