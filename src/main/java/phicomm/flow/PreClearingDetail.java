package phicomm.flow;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import phicomm.model.ClearingInfo;
import phicomm.model.PhicommBuyInfo;
import phicomm.model.PhicommProduct;

public class PreClearingDetail extends FlowBase {

	private String execRequestResult = null;
	private String url;

	public String getExecRequestResult() {
		return execRequestResult;
	}

	public void setExecRequestResult(String execRequestResult) {
		this.execRequestResult = execRequestResult;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

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
				execRequestResult = HttpClientUtil.get(httpConfig.url(url));
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
		PreClearingDetail preClearingDetail = new PreClearingDetail();
		preClearingDetail.execRequestResult = ReadWriteUtil.readToString(DeskTopUtil.getDeskTop(), "b.txt");
		preClearingDetail.after(null, null, null, PhicommConstants.FLOW_SUCCESS);
	}

	public String after(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
				if(StringUtils.isNotBlank(execRequestResult)){
					beforeResult = PhicommConstants.FLOW_BREAK;
					Document document = Jsoup.parse(execRequestResult);
					Elements stateDetas = document.select("div[id=stateDeta] > table");
					if(CollectionUtils.isNotEmpty(stateDetas)){
						for(Element stateDeta : stateDetas){
							Elements datas = stateDeta.select("tbody > tr > td > p > span");
							if(datas.size() == 8){
								ClearingInfo clearingInfo = new ClearingInfo();
								clearingInfo.setUrl(url);
								clearingInfo.setOrderNo(datas.get(5).text());
								clearingInfo.setAmount(BigDecimal.valueOf(Double.valueOf(datas.get(7).text().replaceAll("￥", ""))));
								phicommBuyInfo.addClearingInfos(clearingInfo);
								
							}else{
								FlowUtils.info(String.format("%s,解析出来数据长度不为8", url));
							}
						}
					}
				}
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
