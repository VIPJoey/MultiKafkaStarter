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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

/**
 * KafkaStringMessageTest.
 *
 * @author VIPJoey
 * @date 2023/10/14 下午4:56
 */
@Slf4j
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MmcMultiProducerAutoConfiguration.class, MmcMultiConsumerAutoConfiguration.class,
        DemoService.class, OneProcessor.class})
@TestPropertySource(value = "classpath:application-string.properties")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics = {"${spring.kafka.one.topic}"})
class KafkaStringMessageTest {


    @Value("${spring.kafka.one.topic}")
    private String topicOne;

    @Value("${spring.kafka.two.topic}")
    private String topicTwo;

    @Resource(name = "fourKafkaSender")
    private MmcKafkaSender mmcKafkaSender;


    @Test
    void testDealMessage() throws Exception {

        Thread.sleep(2 * 1000);

        // 模拟生产数据
        produceMessage();

        Thread.sleep(10 * 1000);
    }

    void produceMessage() {


        for (int i = 0; i < 10; i++) {

            DemoMsg msg = new DemoMsg();
            msg.setRoutekey("routekey" + i);
            msg.setName("name" + i);
            msg.setTimestamp(System.currentTimeMillis());

            String json = JsonUtil.toJsonStr(msg);

            mmcKafkaSender.sendStringMessage(topicOne, "aaa", json);


        }
    }
}
