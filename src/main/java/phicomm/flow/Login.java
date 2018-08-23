package phicomm.flow;

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
import phicomm.util.PhicommAccountList;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

public class Login extends FlowBase{
	
	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}else if(PhicommConstants.BUY_TYPE_COUPON.equals(phicommBuyInfo.getBuyType()) || PhicommConstants.BUY_TYPE_QUERY_CLEARING.equals(phicommBuyInfo.getBuyType())||PhicommConstants.BUY_TYPE_GAME.equals(phicommBuyInfo.getBuyType())){
				
			}else if(PhicommConstants.BUY_TYPE_DDCOW.equals(phicommBuyInfo.getBuyType())){
				if(null == PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.get(phicommBuyInfo.getPhone())){
					return PhicommConstants.FLOW_EXIT;
				}
			}else if(null == PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())){
				return PhicommConstants.FLOW_EXIT;
			}
			
			
			Map<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("uname", phicommBuyInfo.getPhone());
			requestParams.put("password", phicommBuyInfo.getPassword());
			requestParams.put("forward", "/");
			httpConfig.headers(HttpHeader.custom()
					.other("X-Requested-With", "XMLHttpRequest")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
					.referer(String.format("%s/passport-login.html", PhicommConstants.PHICOMM_HOST))
					.build()).map(requestParams);
			
			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommBuyConfig.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始登录" , phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "登录中" , phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.post(httpConfig.url(String.format("%s/passport-post_login.html", PhicommConstants.PHICOMM_HOST)));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("登录失败,错误信息:%s", e.getMessage()) , phicommBuyInfo.getBuyType());
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
					if(execRequestResult.contains("Could not connect to the database")){
						beforeResult = PhicommConstants.FLOW_EXIT;
					}else{
						Map map = new Gson().fromJson(execRequestResult , Map.class);
						if(null != map && map.size() > 0){
							if(null != map.get("error") && StringUtils.isNotBlank(map.get("error").toString())){
								execRequestResult = map.get("error").toString();
								if(execRequestResult.contains("账号或密码错误") || execRequestResult.contains("密码错误") || execRequestResult.contains("不存在") || execRequestResult.contains("token") || execRequestResult.contains("手机号格式不正确")){
									beforeResult = PhicommConstants.FLOW_EXIT;
								}
							}else if(null != map.get("success") && StringUtils.isNotBlank(map.get("success").toString())){
								beforeResult = PhicommConstants.FLOW_BREAK;
							}
						}
					}
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "登录成功",phicommBuyInfo.getBuyType());
					PhicommAccountList.PHICOMM_ACCOUNT_MAP.put(phicommBuyInfo.getPhone(), phicommBuyInfo);
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("登录失败返回:%s", execRequestResult),phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("登录失败返回:%s",  execRequestResult) , phicommBuyInfo.getBuyType());
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