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

import java.util.Map;

/**
 * MmcKafkaInputerContainer.
 *
 * @author VIPJoey
 * @since 2023/10/14 下午5:23
 */
public class MmcKafkaInputerContainer {


    private Map<String, MmcInputer> inputers;

    public MmcKafkaInputerContainer(Map<String, MmcInputer> inputers) {

        this.inputers = inputers;
    }
}
