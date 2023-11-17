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
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * AppTest.
 *
 * @author VIPJoey
 * @date 2023/10/14 下午4:56
 */
@Slf4j
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MmcMultiConsumerAutoConfiguration.class, DemoService.class, OneProcessor.class})
@TestPropertySource(value = "classpath:application.properties")
@DirtiesContext
@EmbeddedKafka(topics = {"${spring.kafka.one.topic}"})
class AppTest {


    @Resource
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${spring.kafka.one.topic}")
    private String topicOne;

    @Value("${spring.kafka.two.topic}")
    private String topicTwo;

    @Test
    void testDealMessage() throws Exception {

        // 模拟生产数据
        produceMessage();

        Thread.sleep(10 * 1000);
    }

    void produceMessage() {

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();

        for (int i = 0; i < 10; i++) {

            DemoMsg msg = new DemoMsg();
            msg.setRoutekey("routekey" + i);
            msg.setName("name" + i);
            msg.setTimestamp(System.currentTimeMillis());

            String json = JsonUtil.toJsonStr(msg);
            producer.send(new ProducerRecord<>(topicOne, "my-aggregate-id", json));
            producer.send(new ProducerRecord<>(topicTwo, "my-aggregate-id", json));
            producer.flush();

        }
    }
}
