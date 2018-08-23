package phicomm.core;

import phicomm.config.PhicommConstants;
import logs.OtherUtils;
import phicomm.util.PhicommAccountList;
import phicomm.util.PhicommBlackList;
import phicomm.util.PhicommConfigList;

/**
 * 自动保存配置
 */
public class PhicommAutoSaveConfig extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(PhicommConstants.AUTO_SAVE_CONFIG_TIME);
                OtherUtils.info("------自动保存配置开始运行------");
                //黑名单存储路径
    			PhicommBlackList.write();
    			//配置存储路径
    			PhicommConfigList.write();
    			//用户存储路径
    			PhicommAccountList.write();
                OtherUtils.info("------自动保存配置结束运行------");
            } catch (Exception e) {
                OtherUtils.errorException(String.format("自动保存配置失败:%s",e.getMessage()), e);
            }
        }
    }

    /**
     * 心跳检测
     */
    public static void startAutoSaveConfig(){
    	PhicommAutoSaveConfig phicommAutoSaveConfig = new PhicommAutoSaveConfig();
    	phicommAutoSaveConfig.start();
    }
}
