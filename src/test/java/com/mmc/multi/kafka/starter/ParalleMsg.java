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
 * ParalleMsg.
 *
 * @author VIPJoey
 * @since 2024/9/1 21:13
 */
@Data
class ParalleMsg implements MmcMsgDistinctAware {

    private String routekey;

    private String name;

    private Long timestamp;
}
