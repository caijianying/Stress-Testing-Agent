package com.xiaobaicai.agent.core.plugin.meltdown;

import com.xiaobaicai.agent.core.log.Logger;
import com.xiaobaicai.agent.core.log.LoggerFactory;
import lombok.Getter;

import java.util.concurrent.*;

/**
 * @author xiaobaicai
 * @description 关注微信公众号【程序员小白菜】领取源码
 * @date 2024/12/20 星期五 16:26
 */
public class MeltDownManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeltDownManager.class);

    private static final int SECONDS_TO_MELTDOWN = 10 * 1000;

    private static final ThreadLocal<MeltDownTask> THREAD_LOCAL_TASK = new ThreadLocal<>();

    // 这里的参数按实际情况调整
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

    /**
     * 开启定时任务，检测方法是否已执行
     **/
    public static void meltDownIfNecessary() {
        MeltDownTask meltDownTask = new MeltDownTask();
        THREAD_POOL_EXECUTOR.execute(meltDownTask);
        THREAD_LOCAL_TASK.set(meltDownTask);
    }

    /**
     * 如果超过了10s，则标记熔断
     **/
    public static void markMeltDownFlag() {
        MeltDownTask meltDownTask = THREAD_LOCAL_TASK.get();
        if (meltDownTask != null) {
            try {
                meltDownTask.markMeltDown();
            } finally {
                THREAD_LOCAL_TASK.remove();
            }
        }
    }

    @Getter
    public static class MeltDownTask implements Runnable {
        public long start;

        private volatile boolean meltDown = false;

        private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

        private Integer secondsToMeltDown = SECONDS_TO_MELTDOWN / 1000;

        public MeltDownTask() {
            this.start = System.currentTimeMillis();
        }

        @Override
        public void run() {
            scheduledThreadPool.scheduleAtFixedRate(() -> {
                LOGGER.info(Thread.currentThread().getName() + ": 校验熔断条件...倒计时【" + (--secondsToMeltDown) + "】s");
                boolean moreThan10Seconds = System.currentTimeMillis() - start >= SECONDS_TO_MELTDOWN;
                if (meltDown || moreThan10Seconds) {
                    scheduledThreadPool.shutdown();
                    LOGGER.info(Thread.currentThread().getName() + ": 发起压测熔断，通知压测引擎中止压测任务.");
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        public void markMeltDown() {
            if (System.currentTimeMillis() - start >= SECONDS_TO_MELTDOWN) {
                // 需要做熔断通知的标识
                meltDown = true;
            } else {
                scheduledThreadPool.shutdown();
                LOGGER.info(Thread.currentThread().getName() + ": 方法执行后任务关闭.");
            }
        }
    }
}
