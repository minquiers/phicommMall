package phicomm.flow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;

import io.ReadWriteUtil;
import os.DeskTopUtil;
import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommThreadPool;

public class OrderCenter extends FlowBase {
	
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
			PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "开始进入我的订单" , phicommBuyInfo.getBuyType());
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
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入我的订单中" , phicommBuyInfo.getBuyType());
				execRequestResult = HttpClientUtil.get(httpConfig.url(String.format("%s/my-orders.html", PhicommConstants.PHICOMM_HOST)));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			if(!PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("进入我的订单失败,错误信息:%s", e.getMessage()) , phicommBuyInfo.getBuyType());
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
	
	public static void main(String[] args) throws Exception{
		OrderCenter orderCenter  = new OrderCenter ();
		orderCenter.execRequestResult = ReadWriteUtil.readToString(DeskTopUtil.getDeskTop(), "1.html");
		PhicommBuyInfo phicommBuyInfo = new PhicommBuyInfo();
		phicommBuyInfo.setIp("111");
		phicommBuyInfo.setPhone("222");
		phicommBuyInfo.setBuyType(PhicommConstants.BUY_TYPE_BUY);
		phicommBuyInfo.setFastBuyPage("https://www.phimall.com/index.php/cart-fastbuy-17-1.html");
		Map<String,Object> orderCenterMap = new HashMap<String,Object>();
		orderCenterMap.put("name", "orderCenter"); //流程名称
		orderCenterMap.put("skip", false);  //是否跳过流程
		orderCenterMap.put("errorRepeatExec", true); //错误是否一直执行
		orderCenterMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		orderCenter.after(null, phicommBuyInfo, orderCenterMap, "SUCCESS");
	}
	
	public String after(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				//execRequestResult = ReadWriteUtil.readToString(DeskTopUtil.getDeskTop() + "orders.txt");

				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
				if(StringUtils.isNotBlank(execRequestResult)){
					Document document = Jsoup.parse(execRequestResult);
					Elements elements = document.getElementsByClass("my-orders-list");
					if(null != elements && elements.size() > 0){
					 	Elements activeOrders = elements.get(0).getElementsByClass("status-active");
					 	if(null != activeOrders && activeOrders.size() > 0){
					 		for(Element activeOrder : activeOrders){
					 			boolean existsNonPaymentOrder = false;
					 			Elements nonPaymentOrders = activeOrder.getElementsByClass("btn-danger");
					 			if(null != nonPaymentOrders && nonPaymentOrders.size() > 0){
					 				Element nonPaymentOrder = nonPaymentOrders.get(0);
					 				if(null != nonPaymentOrder && nonPaymentOrder.html().contains("立即付款")){
					 					existsNonPaymentOrder = true;
					 				}
					 			}
					 			if(existsNonPaymentOrder){
					 				existsNonPaymentOrder = false;
					 				String product = null;
					 				String orderNo = null;
						 			Date orderTime = null;
						 			if(null != activeOrder.select("div > a[class=text-muted]") && activeOrder.select("div > a[class=text-muted]").size() > 0){
						 				product = activeOrder.select("div > a[class=text-muted]").get(0).attr("href");
						 				product = product.substring(product.indexOf("-") + 1 , product.indexOf("."));
						 			}
						 			if(null != activeOrder.select("ul[class=list-inline] > li") && activeOrder.select("ul[class=list-inline] > li").size() > 1){
						 				orderNo = activeOrder.select("ul[class=list-inline] > li > a").get(0).html().replaceAll("&nbsp;", "").trim();
						 				orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(activeOrder.select("ul[class=list-inline] > li").get(1).html().trim());
						 			}
						 			//判断和购买的产品相同
						 			if(StringUtils.isNotBlank(product) && StringUtils.isNotBlank(orderNo) && null != orderTime){
						 				String value = phicommBuyInfo.getFastBuyPage().replaceFirst("-", "");
						 				value = value.substring(value.indexOf("-") + 1,value.lastIndexOf("-")).trim();
						 				if(product.equals(value)){
											//orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2018-02-03 19:00");
						 					//判断未失效
						 					if(PhicommConstants.BUY_TYPE_BUY.equals(phicommBuyInfo.getBuyType())){
						 						if(DateUtils.addMinutes(orderTime, PhicommConstants.ORDER_TIME_OUT).after(new Date())){
							 						PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("斐讯订单中心已存在购买相同并且未付款订单,\n订单号:%s,\n订单时间:%s,\n立即支付地址:%s", orderNo , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderTime) , String.format(PhicommConstants.PAYMENT_PATH,PhicommConstants.PHICOMM_HOST, orderNo)) , phicommBuyInfo.getBuyType());
							 						existsNonPaymentOrder = true;
							 						break;
							 					}
						 					}else if(PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
						 						if(orderTime.after(phicommBuyInfo.getCreateTime()) && DateUtils.addMinutes(orderTime, PhicommConstants.ORDER_TIME_OUT).after(new Date())){
						 							FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s订单中心显示抢购成功,立即支付地址-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
						 							PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format(String.format("订单中心显示抢购成功,立即支付地址:%s", String.format(PhicommConstants.PAYMENT_PATH,PhicommConstants.PHICOMM_HOST, orderNo))) , phicommBuyInfo.getBuyType());
							 						existsNonPaymentOrder = true;
							 						beforeResult = PhicommConstants.FLOW_EXIT;
							 						break;
						 						}
						 					}
						 				}
						 			}
						 			if(existsNonPaymentOrder){
						 				break;
						 			}
					 			}
					 		}
					 	}
					}
					if(PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
				 		if(!PhicommConstants.FLOW_EXIT.equals(beforeResult)){
				 			beforeResult = PhicommConstants.FLOW_CONTINUE;
				 		}
				 	}else{
				 		beforeResult = PhicommConstants.FLOW_BREAK;
				 	}
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入我的订单成功" , phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "进入我的订单返回:" + execRequestResult , phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			if(!PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
		 		PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("进入我的订单失败返回:%s", execRequestResult) , phicommBuyInfo.getBuyType());
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