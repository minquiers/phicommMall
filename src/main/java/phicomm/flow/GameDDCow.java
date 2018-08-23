package phicomm.flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.google.gson.Gson;

import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

public class GameDDCow extends FlowBase {
	
	private String execRequestResult = null;
	
	int random[] = {330,327,328,329,328,327,326,325,324,323};
	

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}else if(PhicommConstants.BUY_TYPE_DDCOW.equals(phicommBuyInfo.getBuyType())){
				if(null == PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.get(phicommBuyInfo.getPhone())){
					return PhicommConstants.FLOW_EXIT;
				}
			}else if(null == PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())){
				return PhicommConstants.FLOW_EXIT;
			}
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("killId", phicommBuyInfo.getDdnVersion());
			params.put("mallToken", phicommBuyInfo.getDdnToken());
			httpConfig
			.headers(HttpHeader
					.custom()
					.userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89 VMCHybirdAPP-iOS/2.1.0/")
					.other("Authorization", phicommBuyInfo.getDdnAccessToken())
					.other("X-Requested-With", "XMLHttpRequest")
					.referer(String.format("https://daydaycow.phicomm.com/ddch5/pages/adopt.html?token=%s&uid=%s&access_token=%s", phicommBuyInfo.getDdnToken() , phicommBuyInfo.getDdnUuid() , phicommBuyInfo.getDdnAccessToken()))
					.build())
			.map(params);
			
			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommBuyConfig.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始抢牛", phicommBuyInfo.getBuyType());
			result = PhicommConstants.FLOW_SUCCESS;
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Before失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			if("break".equals(flowMap.get("errorRepeatExec"))){
				result = PhicommConstants.FLOW_BREAK;
			}else if((Boolean) flowMap.get("errorRepeatExec")){
				result = PhicommConstants.FLOW_CONTINUE;
			}else{
				result = PhicommConstants.FLOW_EXIT;
			}
		}
		return result;
	}
	
	public String exec(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Exec-", phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")));
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "抢牛中", phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.post(httpConfig.url(String.format("https://daydaycow.phicomm.com/activity/exeSecKill?_=%s", new Date().getTime())));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢牛失败,错误信息:%s", e.getMessage()), phicommBuyInfo.getBuyType());
			if("break".equals(flowMap.get("errorRepeatExec"))){
				beforeResult = PhicommConstants.FLOW_BREAK;
			}else if((Boolean) flowMap.get("errorRepeatExec")){
				beforeResult = PhicommConstants.FLOW_CONTINUE;
			}else{
				beforeResult = PhicommConstants.FLOW_EXIT;
			}
		}
		return beforeResult;
	}
	
	public String after(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				execRequestResult = unicodeToString(execRequestResult.trim());
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
				if(StringUtils.isNotBlank(execRequestResult)){
					Map<String,Object> map = new Gson().fromJson(execRequestResult, Map.class);
					if(null != map && map.size() > 0){
						//String code = null != map.get("error") ? map.get("error").toString().trim() : null;
						String message = null != map.get("msg") ? map.get("msg").toString().trim() : null;
						execRequestResult = message;
						if(StringUtils.isNotBlank(message) && (message.indexOf("成功") > -1  || message.indexOf("已领养过") > -1 || message.indexOf("请明天") > -1)){
							beforeResult = PhicommConstants.FLOW_BREAK;
						}
					}
				}
				if(!PhicommConstants.FLOW_BREAK.equals(beforeResult) || StringUtils.isBlank(execRequestResult)){
					boolean isIntervalMiddle = isIntervalMiddle(PhicommConstants.DDCOW_CONTINUE_TIME, new Date());
					if(!isIntervalMiddle){
						beforeResult = PhicommConstants.FLOW_BREAK;
						execRequestResult += "已超过系统连续抢购时间。";
					}
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢牛完成,返回:%s", execRequestResult), phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢牛失败返回:%s", execRequestResult) , phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢牛失败返回:%s", execRequestResult),phicommBuyInfo.getBuyType());
			if("break".equals(flowMap.get("errorRepeatExec"))){
				beforeResult = PhicommConstants.FLOW_BREAK;
			}else if((Boolean) flowMap.get("errorRepeatExec")){
				beforeResult = PhicommConstants.FLOW_CONTINUE;
			}else{
				beforeResult = PhicommConstants.FLOW_EXIT;
			}
		}
		return beforeResult;
	}

}
