package phicomm.flow;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.google.gson.Gson;

import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

public class OrderDetail extends FlowBase {
	
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
			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommBuyConfig.getPhone() , flowMap.get("name")));
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始进入订单明细" , phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入订单明细中" , phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.get(httpConfig.url(String.format("%s/checkout-fastbuy.html", PhicommConstants.PHICOMM_HOST)));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("进入订单明细失败,错误信息:%s", e.getMessage()) , phicommBuyInfo.getBuyType());
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
					Document document = Jsoup.parse(execRequestResult);
					Elements cartMd5Elements = document.getElementsByAttributeValueStarting("name", "cart_md5");
					Elements addrIdElements = document.getElementsByAttributeValueStarting("name", "addr_id");
					Elements imgs = document.select("div[class=local-vcode] > img");
					
					if(null != cartMd5Elements && cartMd5Elements.size() > 0){
						phicommBuyInfo.setCartMd5(cartMd5Elements.get(0).val());
					}
					if(null != addrIdElements && addrIdElements.size() > 0){
						phicommBuyInfo.setAddrId(addrIdElements.get(0).val());
					}
					if(null != imgs && imgs.size() > 0){
						String src = imgs.get(0).attr("src");
						if(StringUtils.isNotBlank(src)){
							src = src.replaceAll("/vcode-index-passport", "");
							src = src.substring(0, src.indexOf("."));
						}
						phicommBuyInfo.setMemberId(src);
					}
					if(StringUtils.isNotBlank(phicommBuyInfo.getCartMd5()) && StringUtils.isNotBlank(phicommBuyInfo.getAddrId()) && StringUtils.isNotBlank(phicommBuyInfo.getMemberId())){
						beforeResult = PhicommConstants.FLOW_BREAK;
					}else if(StringUtils.isNotBlank(phicommBuyInfo.getCartMd5()) && StringUtils.isBlank(phicommBuyInfo.getAddrId())){
						execRequestResult = "无默认收货地址,请去斐讯添加";
						beforeResult = PhicommConstants.FLOW_EXIT;
					}else if(StringUtils.isNotBlank(phicommBuyInfo.getCartMd5()) && StringUtils.isNotBlank(phicommBuyInfo.getAddrId())&& StringUtils.isBlank(phicommBuyInfo.getMemberId())){
						execRequestResult = "未找到MemberId";
						beforeResult = PhicommConstants.FLOW_EXIT;
					}else {
						//检测是否下架
						try {
							Map map = new Gson().fromJson(execRequestResult , Map.class);
							if(null != map && map.size() > 0){
								if(null != map.get("error") && StringUtils.isNotBlank(map.get("error").toString())){
									execRequestResult = map.get("error").toString();
									if(execRequestResult.contains("已下架") || execRequestResult.contains("限购商品不能超出") || execRequestResult.contains("plus专享")){
										beforeResult = PhicommConstants.FLOW_EXIT;
									}else if(PhicommConstants.BUY_TYPE_BUY.equals(phicommBuyInfo.getBuyType()) && !isIntervalMiddle(PhicommConstants.BUY_CONTINUE_TIME, new Date())){
										beforeResult = PhicommConstants.FLOW_EXIT;
										execRequestResult += ",已经超过系统连续抢购时间。";
									}else if("立即购买失败.".equals(execRequestResult)){//需要重新添加一次购物车
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
											String addShoppingCartResult = addShoppingCart.before(httpConfig, phicommBuyInfo, addShoppingCartFlowMap);
											addShoppingCartResult = addShoppingCart.exec(httpConfig, phicommBuyInfo, addShoppingCartFlowMap, addShoppingCartResult);
											addShoppingCart.after(httpConfig, phicommBuyInfo, addShoppingCartFlowMap, addShoppingCartResult);
										}
									}
								}
							}
						} catch (Exception e) {
							if(StringUtils.isNotBlank(execRequestResult)){
								try{
									Document doccument = Jsoup.parse(execRequestResult);
									if(null != doccument){
										Elements titles = doccument.getElementsByTag("title");
										if(null != titles && titles.size() > 0){
											String error = titles.get(0).html();
											if(StringUtils.isNotBlank(error)){
												execRequestResult = error;
											}
										}
									}
								}catch(Exception ex){}
							}
							FlowUtils.errorException(String.format("订单明细解析是否下架失败:%s" , e.getMessage()) , e);
						}
					}

				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入订单明细成功" , phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("进入订单明细失败返回:%s", execRequestResult) , phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("进入订单明细失败返回:%s", execRequestResult) , phicommBuyInfo.getBuyType());
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