package phicomm.flow;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

import java.util.Map;

public class VisitGameDDCowHome extends FlowBase {
	
	private String execRequestResult = null;

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
			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommBuyConfig.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始进入抢牛页面" , phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入抢牛页面中" , phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.post(httpConfig.headers(HttpHeader.custom().userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36").build() , true).url(String.format("%s/m/ttnplus.html", PhicommConstants.PHICOMM_HOST)));
				Header headers[] = httpConfig.headers();
				String location = null;
				for(Header header : headers){
					if("location".equals(header.getName())){
						location = header.getValue();
						String urlParams[] = location.substring(location.indexOf("?") + 1).split("&");
						for(String param : urlParams){
							String kvs[] = param.split("=");
							if(kvs.length == 2){
								String key = StringUtils.isNotBlank(kvs[0]) ? kvs[0].trim() : null;
								String value = StringUtils.isNotBlank(kvs[1]) ? kvs[1].trim() : null;
								if("token".equals(key)){
									phicommBuyInfo.setDdnToken(value);
								}else if("access_token".equals(key)){
									phicommBuyInfo.setDdnAccessToken(value);
								}else if("uid".equals(key)){
									phicommBuyInfo.setDdnUuid(value);
								}
							}
						}
						break;
					}
				}
				if(StringUtils.isNotBlank(location)){
					httpConfig.headers(HttpHeader.custom().userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89 VMCHybirdAPP-iOS/2.1.0/").build());
					execRequestResult = HttpClientUtil.get(httpConfig.url(location));
				}
				//https://daydaycow.phicomm.com/ddch5/pages/adopt.html?token=MTA2OjI0NTUwNg==&uid=76623681&access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI3NjYyMzY4MSIsImNvZGUiOiJmZWl4dW4qMTIzLlNIXzI3OTE1MDMiLCJ0eXBlIjoiYWNjZXNzX3Rva2VuIiwiaXNzIjoiUGhpY29tbSIsIm5iZiI6MTUyMzQxNzY2MiwiZXhwIjoxNTIzOTM2MDYyLCJyZWZyZXNoVGltZSI6IjIwMTgtMDQtMTMgMTE6MzQ6MjIifQ.9UKYlaG3XvTlMulmzvHCOS2GE2KMZxop0jJYYGmuiDY
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			if(!PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("进入进入游戏页面失败,错误信息:%s", e.getMessage()) , phicommBuyInfo.getBuyType());
		 	}
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
				//execRequestResult = ReadWriteUtil.readToString(DeskTopUtil.getDeskTop() + "orders.txt");
				
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
				if(StringUtils.isNotBlank(execRequestResult)){
					Document document = Jsoup.parse(execRequestResult);
					if(null != document){
						Elements elements = document.getElementsByTag("title");
						if(null != elements && elements.size() > 0){
							Element element = elements.get(0);
							if(null != element && StringUtils.isNotBlank(element.html())){
								if(element.html().contains("天天牛") && StringUtils.isNotBlank(phicommBuyInfo.getDdnToken()) && StringUtils.isNotBlank(phicommBuyInfo.getDdnAccessToken())){
									beforeResult = PhicommConstants.FLOW_BREAK;
								}
							}
						}
					}
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入抢牛页面成功" , phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入抢牛页面返回:" + execRequestResult , phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			if(!PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
		 		PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("进入抢牛页面失败返回:%s", execRequestResult) , phicommBuyInfo.getBuyType());
		 	}
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
