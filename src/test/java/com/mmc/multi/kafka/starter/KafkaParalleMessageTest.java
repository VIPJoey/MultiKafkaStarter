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

import javax.annotation.Resource;
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

/**
 * KafkaParalleMessageTest.
 *
 * @author tenkye
 * @date 2024/9/1 21:11
 */
@Slf4j
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MmcMultiProducerAutoConfiguration.class, MmcMultiConsumerAutoConfiguration.class,
        FiveProcessor.class})
@TestPropertySource(value = "classpath:application-paralle.properties")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics = {"${spring.kafka.five.topic}"})
public class KafkaParalleMessageTest {


    @Value("${spring.kafka.five.topic}")
    private String fiveTopic;

    @Resource(name = "fiveKafkaSender")
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

            mmcKafkaSender.sendStringMessage(fiveTopic, "aaa", json);


        }
    }
}
