package com.b0c0.common.utils;


import com.b0c0.common.domain.bo.ControlExecuteSpeedBO;
import com.b0c0.common.service.ControlExecuteSpeedService;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author lidongsheng
 * @Description 控制单位时间内发送速率util
 * @date 2021-09-12 12:38
 */
public class ControlExecuteSpeedUtil {

    private static final Logger logger = Logger.getLogger(ControlExecuteSpeedUtil.class.getName());

    /**
     * 同步执行方法
     *
     * @param service
     * @param objects
     */
    public static void execute(ControlExecuteSpeedService service, Object... objects) {
        doExecute(service, objects);
    }

    /**
     * 异步执行
     *
     * @param executor
     * @param service
     * @param objects
     */
    public void syncExecute(ThreadPoolExecutor executor, ControlExecuteSpeedService service, Object... objects) {
        executor.execute(() -> {
            doExecute(service, objects);
        });
    }

    private static void doExecute(ControlExecuteSpeedService service, Object... objects) {
        try {
            service.executeMethod(objects);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 控制发送速率
     *
     * @param millisecond 指定的time时间 毫秒级时间戳
     * @param speed       执行速率
     */
    public static void controlExecuteSpeed(Long millisecond, int speed, ControlExecuteSpeedBO controlExecuteSpeedBO) {
        long beginExecuteTime = controlExecuteSpeedBO.getBeginExecuteTime();
        AtomicInteger sendMqSpeedVar = controlExecuteSpeedBO.getSendMqSpeedVar();
        logger.info("控制发送速率 sendMqSpeedVar -> " + sendMqSpeedVar.get() + " speed -> " + speed);
        // 控制发送速率
        if (sendMqSpeedVar.incrementAndGet() > speed) {
            long endExecuteTime = System.currentTimeMillis();
            long executeTime = endExecuteTime - beginExecuteTime;
            try {
                logger.info("控制发送速率消耗时间 endExecuteTime -> " + endExecuteTime + ",beginExecuteTime -> " + beginExecuteTime + ", executeTime -> " + executeTime);
                if (millisecond - executeTime > 0) {
                    Thread.sleep(millisecond - executeTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMqSpeedVar.getAndSet(0);
            controlExecuteSpeedBO.setBeginExecuteTime(System.currentTimeMillis());
        }
    }

}
