package phicomm.util;

import org.apache.commons.lang3.StringUtils;
import phicomm.config.PhicommConstants;
import phicomm.config.PhicommRebate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhicommLogInfo {
	//传输到前台日志
	public static Map<String,String> PHICOMMLOGINFO = new ConcurrentHashMap<String,String>();
	//心跳检测
	public static Map<String,Long> PHICOMMHEART = new ConcurrentHashMap<String,Long>();
	
	
	public static synchronized String logInfo(String type , String phone , String message , String buyType){
		if((PhicommConstants.BUY_TYPE_BUY_CHECK.equals(buyType)
				&& !StringUtils.contains(message , "抢购成功,立即支付地址"))
			||PhicommConstants.BUY_TYPE_COUPON.equals(buyType)){
			return null;
		}
		if(PhicommConstants.BUY_TYPE_GAME.equals(buyType) ||PhicommConstants.BUY_TYPE_DDCOW.equals(buyType)){
			phone = phone + PhicommConstants.UNDERLINE + buyType;
		} 
		if("get".equals(type)){
			message = PHICOMMLOGINFO.get(phone);
			PHICOMMLOGINFO.remove(phone);
			PHICOMMHEART.remove(phone);
			return message;
		}else{
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			if(message.contains("Read timed out")){
				message = "斐讯服务器返回信息超时";
			}
			if(null != PhicommConstants.REBATE_MODEL && PhicommConstants.REBATE_MODEL.booleanValue()){
				if(null != PhicommRebate.PHICOMM_ACCOUNT_REBATE_MAP && null != PhicommConstants.REBATE_TELEPHONE && StringUtils.isNotBlank(PhicommRebate.PHICOMM_ACCOUNT_REBATE_MAP.get(PhicommConstants.REBATE_TELEPHONE))){
					String reateStr = String.format("?vsid=%s" , PhicommRebate.PHICOMM_ACCOUNT_REBATE_MAP.get(PhicommConstants.REBATE_TELEPHONE));
					if(message.contains(reateStr)){
						message = message.replaceAll(reateStr , "");
					}
				}
			}

			if(StringUtils.isNoneBlank(PHICOMMLOGINFO.get(phone))){
				PHICOMMLOGINFO.put(phone, String.format("%s%s - %s\n",PHICOMMLOGINFO.get(phone) , date , message));
			}else{
				PHICOMMLOGINFO.put(phone, String.format("%s - %s\n" , date , message));
			}
			if(null == PHICOMMHEART.get(phone)){
				PHICOMMHEART.put(phone , new Date().getTime());
			}
			return null;
		}
	}

}
