package phicomm.flow;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommProduct;

import java.util.Date;
import java.util.Map;

public class QuerySku extends FlowBase {

	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommProduct phicommProduct, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}

			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommSkuInfo.getPhone() , flowMap.get("name")));
			result = PhicommConstants.FLOW_SUCCESS;
		}catch(Exception e){
			FlowUtils.errorException(String.format("-流程:%s-产品:%s%s-Exec失败-",  flowMap.get("name"),phicommProduct.getName(),phicommProduct.getColor()) ,e);
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

	public String exec(HttpConfig httpConfig , PhicommProduct phicommProduct, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				FlowUtils.info(String.format("-流程:%s-产品:%s%s-Exec-",  flowMap.get("name"),phicommProduct.getName(),phicommProduct.getColor()));
				execRequestResult = HttpClientUtil.get(httpConfig.url(String.format(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/openapi/stock/confirm?sku=%s") , phicommProduct.getSkuCode())));
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("-流程:%s-产品:%s%s-Exec失败-",  flowMap.get("name"),phicommProduct.getName(),phicommProduct.getColor())  ,e);
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

	public String after(HttpConfig httpConfig , PhicommProduct phicommProduct, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				execRequestResult = unicodeToString(execRequestResult.trim());
				FlowUtils.info(String.format("-流程:%s-产品:%s%s-After-%s-", flowMap.get("name"),phicommProduct.getName(),phicommProduct.getColor(),execRequestResult));
				if(StringUtils.isNotBlank(execRequestResult)){
					beforeResult = PhicommConstants.FLOW_BREAK;

					Map<String,Object> resultMap = new Gson().fromJson(execRequestResult , Map.class);
					if(MapUtils.isNotEmpty(resultMap) && "success".equals(resultMap.get("result"))){
						if(resultMap.get("data") instanceof Map) {
							Map<String, Object> skuInfoMap = (Map<String, Object>) resultMap.get("data");
							if (MapUtils.isNotEmpty(skuInfoMap) && null != skuInfoMap.get(phicommProduct.getSkuCode())) {
								Map<String, Object> skuMap = (Map<String, Object>) skuInfoMap.get(phicommProduct.getSkuCode());
								if (MapUtils.isNotEmpty(skuMap) && null != skuMap.get("num")) {
									Long sku = Long.valueOf(skuMap.get("num").toString());
									phicommProduct.setSkuQueryDate(new Date());
									if (null == phicommProduct.getSku() || phicommProduct.getSku().longValue() != sku.longValue()) {
										phicommProduct.setSku(sku);
										phicommProduct.skuChange();
										if(sku.longValue() <= 0){ //无库存不通知
											phicommProduct.setSkuChangeFlag(0);
											phicommProduct.setSkuChangeAlertInfo(null);
										}
									} else {
										phicommProduct.setSkuChangeFlag(0);
										phicommProduct.setSkuChangeAlertInfo(null);
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("-流程:%s-产品:%s%s-After失败-" , flowMap.get("name") , phicommProduct.getName(),phicommProduct.getColor()) ,e);
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
