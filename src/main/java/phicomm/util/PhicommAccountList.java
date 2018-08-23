package phicomm.util;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.ReadWriteUtil;
import phicomm.config.PhicommConstants;
import logs.OtherUtils;
import phicomm.model.PhicommBuyInfo;

/**
 * 账户信息
 * @author Administrator
 *
 */
public class PhicommAccountList {
	public static Map<String,PhicommBuyInfo> PHICOMM_ACCOUNT_MAP = new ConcurrentHashMap<String,PhicommBuyInfo>();
	
	public static void load(){
		try {
			String json = ReadWriteUtil.readToString(PhicommConstants.USER_LIST_PATH);
			if(StringUtils.isNotBlank(json)){
				Type listType = new TypeToken<ConcurrentHashMap<String,PhicommBuyInfo>>(){}.getType();
				PHICOMM_ACCOUNT_MAP = new Gson().fromJson(json, listType);
			}
			if(null == PHICOMM_ACCOUNT_MAP){
				PHICOMM_ACCOUNT_MAP = new ConcurrentHashMap<String,PhicommBuyInfo>();
			}
		} catch (Exception e) {
			OtherUtils.errorException(String.format("缓存载入斐讯账号信息失败:%s",e.getMessage()), e);
		}
	}
	
	public static void write(){
		try {
			String json = ReadWriteUtil.readToString(PhicommConstants.USER_LIST_PATH);
			Map<String,PhicommBuyInfo> temp = null;
			if(StringUtils.isNotBlank(json)){
				temp = new Gson().fromJson(json, new ConcurrentHashMap<String,PhicommBuyInfo>().getClass());
			}
			if(null == temp){
				temp = new ConcurrentHashMap<String,PhicommBuyInfo>();
			}
			
			for(String phone : PHICOMM_ACCOUNT_MAP.keySet()){
				temp.put(phone, PHICOMM_ACCOUNT_MAP.get(phone));
			}
			ReadWriteUtil.write(new Gson().toJson(temp), PhicommConstants.USER_LIST_PATH);
		} catch (Exception e) {
			OtherUtils.errorException(String.format("缓存写入斐讯账号信息失败:%s",e.getMessage()), e);
		}
	}
}
