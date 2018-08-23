package phicomm.flow;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.arronlong.httpclientutil.common.HttpConfig;

import io.ReadWriteUtil;
import os.DeskTopUtil;
import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;

public class QueryClearing extends FlowBase {

	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}

			//FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Before-",phicommBuyConfig.getIp(),  phicommSkuInfo.getPhone() , flowMap.get("name")));
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
			
			
				PreClearing preClearing = new PreClearing();
				while(true){
					int execBeforeSize = null == phicommBuyInfo.getPreClearings() ? 0 : phicommBuyInfo.getPreClearings().size();
					preClearing.before(httpConfig, phicommBuyInfo, flowMap);
					preClearing.exec(httpConfig, phicommBuyInfo, flowMap, beforeResult);
					preClearing.after(httpConfig, phicommBuyInfo, flowMap, beforeResult);
					if(execBeforeSize == (null == phicommBuyInfo.getPreClearings() ? 0 : phicommBuyInfo.getPreClearings().size())){
						break;
					}else{
						preClearing.setPage(preClearing.getPage() + 1);
					}
				}
				if(CollectionUtils.isNotEmpty(phicommBuyInfo.getPreClearings())){
					PreClearingDetail preClearingDetail = null;
					for(String url : phicommBuyInfo.getPreClearings()){
						preClearingDetail = new PreClearingDetail();
						preClearingDetail.setUrl(url);
						preClearingDetail.before(httpConfig, phicommBuyInfo, flowMap);
						preClearingDetail.exec(httpConfig, phicommBuyInfo, flowMap, beforeResult);
						preClearingDetail.after(httpConfig, phicommBuyInfo, flowMap, beforeResult);
					}
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
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
		QueryClearing settlementSettled = new QueryClearing();
		settlementSettled.execRequestResult = ReadWriteUtil.readToString(DeskTopUtil.getDeskTop(), "a.txt");
		settlementSettled.after(null, null, null, PhicommConstants.FLOW_SUCCESS);
	}

	public String after(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
				beforeResult = PhicommConstants.FLOW_BREAK;
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
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
