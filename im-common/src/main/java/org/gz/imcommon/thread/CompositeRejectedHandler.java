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
 * @author 17853
 */
public class CompositeRejectedHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        r.run();
    }

}
