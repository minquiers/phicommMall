package phicomm.flow;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

import java.util.Date;
import java.util.Map;

public class Buy extends FlowBase {
	
	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}else if(null == PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())){
				return PhicommConstants.FLOW_EXIT;
			}
			
			httpConfig
			.headers(HttpHeader
					.custom()
					.other("X-Requested-With", "XMLHttpRequest")
					.referer(String.format("%s/checkout-fastbuy.html", PhicommConstants.PHICOMM_HOST))
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
					.build())
			.url(String.format("%s/order-create-is_fastbuy.html", PhicommConstants.PHICOMM_HOST))
			.map((new Gson()
					.fromJson(new GsonBuilder()
							.excludeFieldsWithoutExposeAnnotation()
							.create()
							.toJson(phicommBuyInfo), Map.class)));
			
			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始抢购",phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "抢购中",phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.post(httpConfig.url(String.format("%s/order-create-is_fastbuy.html", PhicommConstants.PHICOMM_HOST)));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢购失败,错误信息:%s", e.getMessage()),phicommBuyInfo.getBuyType());
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
				String doPaymentPath = null;
				if(StringUtils.isNotBlank(execRequestResult)){
					Map map = new Gson().fromJson(execRequestResult , Map.class);
					if(null != map && map.size() > 0){
						if(null != map.get("error") && StringUtils.isNotBlank(map.get("error").toString())){
							execRequestResult = map.get("error").toString();
							if(execRequestResult.contains("账号异常")
									|| execRequestResult.contains("没有可结算商品")
									|| execRequestResult.contains("限购")){
								beforeResult = PhicommConstants.FLOW_EXIT;
							}else if(PhicommConstants.BUY_TYPE_BUY.equals(phicommBuyInfo.getBuyType()) && !isIntervalMiddle(PhicommConstants.BUY_CONTINUE_TIME, new Date())){
								execRequestResult += ",已超过系统连续抢购时间。";
								beforeResult = PhicommConstants.FLOW_EXIT;
							}else if(execRequestResult.contains("验证码错误")){
								Map<String,Object> buyVcodeFlowMap = null;
								if(CollectionUtils.isNotEmpty(PhicommFlow.PHICOMM_BUY_FLOW)){
									for(int i = 0 ; i < PhicommFlow.PHICOMM_BUY_FLOW.size();i++){
										Map<String,Object> flow = PhicommFlow.PHICOMM_BUY_FLOW.get(i);
										if(null != flow && "buyVcode".equals(flow.get("name"))){
											buyVcodeFlowMap = flow;
											break;
										}
									}
								}
								if(null != buyVcodeFlowMap){
									BuyVcode buyVcode = new BuyVcode();
									String buyVcodeResult = buyVcode.before(httpConfig , phicommBuyInfo , buyVcodeFlowMap);
									buyVcodeResult = buyVcode.exec(httpConfig , phicommBuyInfo , buyVcodeFlowMap,buyVcodeResult);
									buyVcode.after(httpConfig , phicommBuyInfo , buyVcodeFlowMap , buyVcodeResult);
								}
							}else if(execRequestResult.contains("购物车发生变化")){
								Map<String,Object> addShoppingCartFlowMap = null;
								if(CollectionUtils.isNotEmpty(PhicommFlow.PHICOMM_BUY_FLOW)){
									for(int i = 0 ; i < PhicommFlow.PHICOMM_BUY_FLOW.size();i++){
										Map<String,Object> flow = PhicommFlow.PHICOMM_BUY_FLOW.get(i);
										if(null != flow && "addShoppingCart".equals(flow.get("name"))){
											addShoppingCartFlowMap = flow;
											break;
										}
									}
								}
								if(null != addShoppingCartFlowMap){
									AddShoppingCart addShoppingCart = new AddShoppingCart();
									String addShoppingCartResult = addShoppingCart.before(httpConfig , phicommBuyInfo , addShoppingCartFlowMap);
									addShoppingCartResult = addShoppingCart.exec(httpConfig , phicommBuyInfo , addShoppingCartFlowMap,addShoppingCartResult);
									addShoppingCart.after(httpConfig , phicommBuyInfo , addShoppingCartFlowMap , addShoppingCartResult);
								}
							}
						}else if(null != map.get("success") && StringUtils.isNotBlank(map.get("success").toString())
								&& null != map.get("redirect") && StringUtils.isNotBlank(map.get("redirect").toString())){
							doPaymentPath = map.get("redirect").toString().replaceAll("-payment-", "-dopayment-");
							beforeResult = PhicommConstants.FLOW_BREAK;
						}
					}
					
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					//{"success":"订单提交成功","redirect":"\/checkout-payment-3209595841107-1.html?vsid=36134127481"}
					if(doPaymentPath.contains("?vsid")){
						doPaymentPath = doPaymentPath.substring(0, doPaymentPath.indexOf("?vsid"));
					}
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢购成功,立即支付地址:  %s%s",PhicommConstants.PHICOMM_HOST, doPaymentPath),phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢购失败返回:%s", execRequestResult) ,phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("抢购失败返回:%s", execRequestResult) , phicommBuyInfo.getBuyType());
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