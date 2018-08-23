package phicomm.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;

import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

public class VisitCouponPage extends FlowBase {
	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}else if(PhicommConstants.BUY_TYPE_COUPON.equals(phicommBuyInfo.getBuyType())){

			}else if(null == PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())){
				return PhicommConstants.FLOW_EXIT;
			}

			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommBuyConfig.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始访问优惠券页面",phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "访问优惠券页面中",phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.post(httpConfig.url(String.format("%s/my-coupon.html", PhicommConstants.PHICOMM_HOST)));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("访问优惠券页面失败,错误信息:%s", e.getMessage()),phicommBuyInfo.getBuyType());
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
				if(StringUtils.isNotBlank(execRequestResult)){
					phicommBuyInfo.setCouponList(parseHtmlToCoupon(execRequestResult));
					beforeResult = PhicommConstants.FLOW_BREAK;
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "访问优惠券页面成功",phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("访问优惠券页面失败返回:%s", execRequestResult) ,phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("访问优惠券页面失败返回:%s", execRequestResult) ,phicommBuyInfo.getBuyType());
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

	List<Map<String,String>> parseHtmlToCoupon(String html){
		List<Map<String, String>> couponList = null;
		try {
			Document document = Jsoup.parse(html);
			if (null != document) {
				Elements elements = document.select("li[class=brand_new] > div[class=list-group-item]");
				if (null != elements && elements.size() > 0) {
					couponList = new ArrayList<Map<String, String>>();
					for (int i = 0; i < elements.size(); i++) {
						Map<String, String> map = new HashMap<String, String>();
						String couponHTML = elements.get(i).html()
								.replaceAll("<!--", "")
								.replaceAll("-->", "");
						Document couponDocument = Jsoup.parse(couponHTML);
						Elements labels = couponDocument.getElementsByClass("label");
						if(null != labels && labels.size() > 1) {
							String status = couponDocument.getElementsByClass("label").get(0).text() + "-" + couponDocument.getElementsByClass("label").get(1).text();
							if ("有效-未使用".equals(status)) {
								String code = couponDocument.getElementsByTag("h3").get(0).text();
								String name = couponDocument.getElementsByClass("coupon_sum").attr("title");
								if(couponDocument.getElementsByTag("h3").size() == 2){
									name = couponDocument.getElementsByTag("h3").get(1).text() + " " + name;
								}
								String lastUseDate = couponDocument.getElementsByTag("li").get(1).text();
								map.put("code", code);
								map.put("name", name);
								map.put("lastUseDate", lastUseDate);
								couponList.add(map);
							}
						}
					}
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(e.getMessage() ,e);
		}
		if(null != couponList){
			if(0 == couponList.size() || null == couponList.get(0) || couponList.get(0).size() == 0){
				couponList = null;
			}
		}
		return couponList;
	}
}
