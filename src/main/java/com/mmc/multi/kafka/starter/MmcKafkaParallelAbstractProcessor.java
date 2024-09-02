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

import com.mmc.juc.MmcTask;
import com.mmc.juc.MmcTaskExecutor;
import com.mmc.juc.RateLimiter;
import com.mmc.juc.TokenBucket;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * MmcKafkaParallelAbstractProcessor.
 *
 * @author VIPJoey
 * @date 2024/9/1 20:14
 */
@Slf4j
@Setter
public abstract class MmcKafkaParallelAbstractProcessor<T, R> extends MmcKafkaAbstractProcessor<T> {

    /**
     * taskExecutor.
     */
    protected MmcTaskExecutor<T, R> taskExecutor;

    /**
     * init.
     */
    public void init() {

        ContainerConfig config = properties.getContainer();

        this.taskExecutor = MmcTaskExecutor.<T, R>builder()
                .taskProcessor(this::handelBatchDatas)
                .threshold(config.getThreshold())
                .rateLimiter(buildRateLimiter(config.getRate()))
                .taskMerger(this::mergeResult)
                .forkJoinPoolConcurrency(config.getParallelism())
                .build();
    }


    @Override
    protected void dealMessage(List<T> datas) throws ExecutionException, InterruptedException {

        if (properties.getContainer().isEnabled()) {

            // 开启并发处理
            R result = taskExecutor.execute(MmcTask.<T, R>builder()
                    .taskSource(datas)
                    .taskName(getTaskName(datas))
                    .build()
            );

            dealMessageCallBack(result);

        } else {

            // 同步处理
            R result = handelBatchDatas(datas);
            dealMessageCallBack(result);

        }
    }

    /**
     * 合并小任务结果（默认不合并）.
     *
     * @param left 左边处理结果
     * @param right 右边处理结果
     * @return 合并后的结果
     */
    protected R mergeResult(R left, R right) {
        return null;
    }

    /**
     * 构建速率限制器.
     *
     * @param rate qps
     * @return 速率限制器
     */
    protected RateLimiter buildRateLimiter(int rate) {
        return new TokenBucket(rate, rate);
    }

    /**
     * 当所有消息处理完后，会调用该方法.
     *
     * @param result 处理结果
     */
    protected void dealMessageCallBack(R result) {
        // default null
    }

    /**
     * 获取任务名称.
     */
    protected String getTaskName(List<T> datas) {
        return name;
    }


    /**
     * 真正处理消息的方法.
     *
     * @param datas 待处理消息
     * @return 小任务处理完的结果
     */
    protected abstract R handelBatchDatas(List<T> datas);

}
