package phicomm.core;

import phicomm.config.PhicommConstants;
import logs.OtherUtils;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

import java.util.Date;

/**
 * 心跳检测
 */
public class PhicommHeartBeat extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(PhicommConstants.HEART_BEAT_TIME);
                OtherUtils.info("------心跳检测程序开始运行------");
                //清除5分钟无心跳的程序(5分钟运行一次,3分钟内没有心跳的移除)
                for (String phone : PhicommLogInfo.PHICOMMHEART.keySet()) {
                    Long createTime = PhicommLogInfo.PHICOMMHEART.get(phone);
                    if (null != createTime && (new Date().getTime() - createTime.longValue()) >= PhicommConstants.HEART_BEAT_TIME_OUT) {
                        PhicommThreadPool.PHICOMMHTREADPOOL.remove(phone);
                        OtherUtils.info(String.format("------[移除无心跳-%s]------" , phone));
                        PhicommLogInfo.logInfo("get", phone, null,null);
                    }
                }
                OtherUtils.info("------心跳检测程序结束运行------");
            } catch (Exception e) {
                OtherUtils.errorException(String.format("心跳检测失败:%s",e.getMessage()), e);
            }
        }
    }

    /**
     * 心跳检测
     */
    public static void startCheck(){
        PhicommHeartBeat phicommHeartBeat = new PhicommHeartBeat();
        phicommHeartBeat.start();
    }
}
