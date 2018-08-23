package phicomm.flow;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;

import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

public class GetGameScore extends FlowBase {
	
	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}else if(PhicommConstants.BUY_TYPE_COUPON.equals(phicommBuyInfo.getBuyType()) || PhicommConstants.BUY_TYPE_GAME.equals(phicommBuyInfo.getBuyType())){
				
			}else if(null == PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())){
				return PhicommConstants.FLOW_EXIT;
			}
			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommBuyConfig.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始查询游戏分数",phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "查询游戏分数中",phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.get(httpConfig.url("http://testmdgc.phicommall.com/mdgc/gameResult"));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("查询游戏分数失败,错误信息:%s", e.getMessage()),phicommBuyInfo.getBuyType());
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
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
				Integer score = null;
				if(StringUtils.isNotBlank(execRequestResult)){
					Document document = Jsoup.parse(execRequestResult);
					if(null != document){
						Element totalScoreElement = document.getElementById("total_score");
						Elements totalScoreElements = document.select("div.canExchange > h4 > span");
						if(null != totalScoreElement && StringUtils.isNotBlank(totalScoreElement.html())){
							score = Integer.valueOf(totalScoreElement.html().trim());
							beforeResult = PhicommConstants.FLOW_BREAK;
						}else if(null != totalScoreElements && totalScoreElements.size() > 0){
							score = Integer.valueOf(totalScoreElements.get(0).html().trim());
							beforeResult = PhicommConstants.FLOW_BREAK;
						}
					}
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("查询游戏分数成功,当前分数:%s%s", score,(null != score && score >= 1999 ? ",特别提示【已获得加速券领取资格】" : "")),phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("查询游戏分数失败返回:%s", execRequestResult) ,phicommBuyInfo.getBuyType() );
					beforeResult = PhicommConstants.FLOW_BREAK;
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("查询游戏分数失败返回:%s", execRequestResult) ,phicommBuyInfo.getBuyType());
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
