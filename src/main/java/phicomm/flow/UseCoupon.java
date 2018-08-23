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
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

public class UseCoupon extends FlowBase {
	
	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip") || StringUtils.isBlank(phicommBuyInfo.getCoupon())){
				return PhicommConstants.FLOW_BREAK;
			}else if(null == PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())){
				return PhicommConstants.FLOW_EXIT;
			}else if(PhicommConstants.COUPON_USE_ERROR_MAX_COUNT <= phicommBuyInfo.getCouponUseErrorCoupon()){
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("使用优惠券超过最大次数(%s次),跳过使用优惠券流程", PhicommConstants.COUPON_USE_ERROR_MAX_COUNT), phicommBuyInfo.getBuyType());
				return PhicommConstants.FLOW_BREAK;
			}
			Map<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("coupon", phicommBuyInfo.getCoupon());
			httpConfig
			.headers( HttpHeader.custom()
					.other("X-Requested-With", "XMLHttpRequest")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
					.referer(String.format("%s/checkout-fastbuy.html", PhicommConstants.PHICOMM_HOST))
					.build())
			.map(requestParams);
			
			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommBuyConfig.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始使用优惠券" , phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "使用优惠券中" , phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.post(httpConfig.url(String.format("%s/cart-use_coupon-is_fastbuy.html", PhicommConstants.PHICOMM_HOST)));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("使用优惠券失败,返回:%s", e.getMessage()) , phicommBuyInfo.getBuyType());
			//phicommBuyInfo.setCouponUseErrorCoupon(phicommBuyInfo.getCouponUseErrorCoupon() + 1);
			//PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("使用优惠券(%s次)失败,返回:%s", phicommBuyInfo.getCouponUseErrorCoupon() , e.getMessage()) , phicommBuyInfo.getBuyType());
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
					if(null != map && map.size() > 0 ){
						if(null != map.get("data") && StringUtils.isNotBlank(map.get("data").toString())){
							Map<String,Object> data = (Map<String, Object>) map.get("data");
							if(null != data && null != data.get("new_cart_md5") && StringUtils.isNotBlank(data.get("new_cart_md5").toString())){
								phicommBuyInfo.setCartMd5(data.get("new_cart_md5").toString().trim());
								beforeResult = PhicommConstants.FLOW_BREAK;
							}
						}else if(null != map.get("error")
								&& StringUtils.isNotBlank(map.get("error").toString())
								&& (map.get("error").toString().contains("无效的优惠券")
									|| map.get("error").toString().contains("该优惠券已被其他会员认领"))){
							beforeResult = PhicommConstants.FLOW_BREAK;
							PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("使用优惠券失败,返回:%s",  execRequestResult), phicommBuyInfo.getBuyType());
							return beforeResult;
						}
					}
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "使用优惠券成功" , phicommBuyInfo.getBuyType());
				}else{
					phicommBuyInfo.setCouponUseErrorCoupon(phicommBuyInfo.getCouponUseErrorCoupon() + 1);
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("使用优惠券(%s次)失败,返回:%s", phicommBuyInfo.getCouponUseErrorCoupon() , execRequestResult), phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			phicommBuyInfo.setCouponUseErrorCoupon(phicommBuyInfo.getCouponUseErrorCoupon() + 1);
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("使用优惠券(%s次)失败,返回:%s", phicommBuyInfo.getCouponUseErrorCoupon() , execRequestResult),phicommBuyInfo.getBuyType());
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
