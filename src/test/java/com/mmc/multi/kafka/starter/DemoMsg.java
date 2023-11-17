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

import lombok.Data;

/**
 * DemoMsg.
 *
 * @author VIPJoey
 * @date 2023/10/29 上午10:27
 */
@Data
class DemoMsg implements MmcKafkaMsg {

    private String routekey;

    private String name;

    private Long timestamp;

}
