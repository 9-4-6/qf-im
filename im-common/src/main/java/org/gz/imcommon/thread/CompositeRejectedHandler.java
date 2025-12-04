package org.gz.imcommon.thread;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义组合拒绝策略
 *
 * 方案1根据cpu负载选择拒绝方式
 * 方案2 动态监控线程池，进行动态调控线程参数 （推荐）
 * 方案3 业务进行判断如果当前线程大于最大线程，进行睡眠会，再进行
 * 方案4 再次把任务放到线程池再次执行
 *
 *
 */
public class CompositeRejectedHandler implements RejectedExecutionHandler {
    private final RejectedExecutionHandler primaryHandler = new ThreadPoolExecutor.CallerRunsPolicy();
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        // 当系统负载低于70%时，使用主策略（调用者执行）
        if (getSystemLoad() < 0.7) {
            primaryHandler.rejectedExecution(r, executor);
        } else {
            // todo 当系统负载过高（≥70%）时，使用备用策略（自定义丢弃/持久化）

        }
    }
    // 获取当前系统CPU负载
    private double getSystemLoad() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                    OperatingSystemMXBean.class
            );
            return osBean.getCpuLoad();
        } catch (Exception e) {
            // 如果获取CPU负载失败，默认返回高负载
            System.err.println("获取CPU负载失败: " + e.getMessage());
            return 1.0; // 返回高负载
        }
    }
}
