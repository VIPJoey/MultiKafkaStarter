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

import com.mmc.multi.kafka.starter.proto.DemoPb;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
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
 * KafkaPbMessageTest.
 *
 * @author VIPJoey
 * @since 2024/6/2 11:14
 */

@Slf4j
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MmcMultiConsumerAutoConfiguration.class, DemoService.class, PbProcessor.class})
@TestPropertySource(value = "classpath:application-pb.properties")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics = {"${spring.kafka.pb.topic}"})
class KafkaPbMessageTest {


    @Resource
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${spring.kafka.pb.topic}")
    private String topicPb;


    @Test
    void testDealMessage() throws Exception {

        Thread.sleep(2 * 1000);

        // 模拟生产数据
        produceMessage();

        Thread.sleep(10 * 1000);
    }

    void produceMessage() {


        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, byte[]> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new ByteArraySerializer()).createProducer();


        for (int i = 0; i < 10; i++) {

            DemoPb.PbMsg msg = DemoPb.PbMsg.newBuilder()
                    .setCosImgUrl("http://google.com")
                    .setRoutekey("routekey-" + i).build();


            producer.send(new ProducerRecord<>(topicPb, "my-aggregate-id", msg.toByteArray()));
            producer.flush();
        }


    }
}