package com.b0c0.common.domain.bo;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lidongsheng
 * @Description 控制分账速率bo
 * @date 2021-09-26 16:47
 */
public class ControlExecuteSpeedBO {
    /**
     * 单位时间内分账数量
     */
    private AtomicInteger sendMqSpeedVar;
    /**
     * 开始时间
     */
    private long beginExecuteTime;

    public ControlExecuteSpeedBO(){
        sendMqSpeedVar = new AtomicInteger(0);
        beginExecuteTime = System.currentTimeMillis();
    }

    public AtomicInteger getSendMqSpeedVar() {
        return sendMqSpeedVar;
    }

    public void setSendMqSpeedVar(AtomicInteger sendMqSpeedVar) {
        this.sendMqSpeedVar = sendMqSpeedVar;
    }

    public long getBeginExecuteTime() {
        return beginExecuteTime;
    }

    public void setBeginExecuteTime(long beginExecuteTime) {
        this.beginExecuteTime = beginExecuteTime;
    }
}
