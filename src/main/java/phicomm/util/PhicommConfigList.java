package phicomm.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.ReadWriteUtil;
import org.apache.commons.lang3.StringUtils;
import phicomm.config.PhicommConstants;
import logs.OtherUtils;
import phicomm.model.PhicommConfig;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PhicommConfigList {
    public static List<PhicommConfig> PHICOMM_CONFIG_LIST = null;

    public static synchronized void init() {
        if (null == PHICOMM_CONFIG_LIST) {
            PHICOMM_CONFIG_LIST = new CopyOnWriteArrayList<PhicommConfig>();
        }
    }
    

    public static void load(){
        try {
        	String json = ReadWriteUtil.readToString(PhicommConstants.CONFIG_LIST_PATH);
        	if(StringUtils.isNotBlank(json)){
        		Type listType = new TypeToken<CopyOnWriteArrayList<PhicommConfig>>(){}.getType();
        		PHICOMM_CONFIG_LIST = new Gson().fromJson(json, listType);
        		if(null != PHICOMM_CONFIG_LIST && PHICOMM_CONFIG_LIST.size() > 0) {
                    for (PhicommConfig phicommConfig : PHICOMM_CONFIG_LIST) {
                        phicommConfig.setRef(phicommConfig.getRef());
                    }
                }
        	}
        } catch (Exception e) {
           OtherUtils.errorException(String.format("缓存载入斐讯配置信息失败:%s",e.getMessage()),e);
        }
    }

    public static void write(){
        try {
            if(null != PHICOMM_CONFIG_LIST){
                String json = new Gson().toJson(PHICOMM_CONFIG_LIST);
                ReadWriteUtil.write(json, PhicommConstants.CONFIG_LIST_PATH);
            }
        } catch (Exception e) {
            OtherUtils.errorException(String.format("缓存写入斐讯配置信息失败:%s",e.getMessage()),e);
        }
    }
}
