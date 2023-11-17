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

import org.springframework.util.StringUtils;

import java.util.List;

/**
 * CommonUtil.
 *
 * @author VIPJoey
 * @date 2023/10/15 下午4:47
 */
class CommonUtil {

    /**
     * 是否为空.
     */
    public static boolean isNotEmpty(List<?> collection) {

        return !isEmpty(collection);
    }

    /**
     * 是否为空.
     */
    public static boolean isNotBlank(String source) {
        return StringUtils.hasText(source);
    }

    /**
     * 是否不为空.
     */
    public static boolean isBlank(String source) {
        return !isNotBlank(source);
    }

    /**
     * 是否为空.
     */
    public static boolean isEmpty(List<?> sources) {

        return null == sources || sources.isEmpty();
    }
}