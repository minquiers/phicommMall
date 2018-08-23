package phicomm.util;

import io.ReadWriteUtil;
import org.apache.commons.lang3.StringUtils;
import phicomm.config.PhicommConstants;
import logs.OtherUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 黑名单
 * @author Administrator
 *
 */
public class PhicommBlackList {
	public static Map<String,String> PHICOMM_BLACK_MAP = new ConcurrentHashMap<String,String>();
	
	public static void load(){
		try {
			List<String> list = ReadWriteUtil.read(PhicommConstants.BLACK_LIST_PATH);
			for(String ip : list){
				PHICOMM_BLACK_MAP.put(ip, "");
			}
		} catch (Exception e) {
			OtherUtils.errorException(String.format("缓存载入斐讯黑名单信息失败:%s",e.getMessage()), e);
		}
	}
	
	public static void write(){
		try {
			List<String> list = ReadWriteUtil.read(PhicommConstants.BLACK_LIST_PATH);
			for(String ip : PHICOMM_BLACK_MAP.keySet()){
				if(StringUtils.isNotBlank(ip) && !list.contains(ip.trim())){
					list.add(ip);
				}
			}
			ReadWriteUtil.writeList(list, PhicommConstants.BLACK_LIST_PATH);
		} catch (Exception e) {
			OtherUtils.errorException(String.format("缓存写入斐讯黑名单信息失败:%s",e.getMessage()), e);
		}
	}
	
}
