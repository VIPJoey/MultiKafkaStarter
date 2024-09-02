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

/**
 * ContainerConfig.
 *
 * @author VIPJoey
 * @date 2024/9/1 20:40
 */
public interface ContainerConfig {

    /**
     * Get the execute rate.
     *
     * @return rate
     */
    int getRate();

    /**
     * Get the max task count for per thread.
     *
     * @return max count
     */
    int getThreshold();


    /**
     * The max thread count, default is numbers of processor.
     * @return count
     */
    default int getParallelism() {
        return Runtime.getRuntime().availableProcessors();
    }
}
