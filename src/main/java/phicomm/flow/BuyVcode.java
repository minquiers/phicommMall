package phicomm.flow;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
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
import util.ImageBase64Utils;
import vcode.VCode;

/**
 * 购买获取验证码
 * @author Administrator
 *
 */
public class BuyVcode extends FlowBase {
	
	private String execRequestResult = null;

	public String before(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap){
		String result = PhicommConstants.FLOW_CONTINUE;
		try{
			//判断是否跳过
			if((Boolean) flowMap.get("skip")){
				return PhicommConstants.FLOW_BREAK;
			}else if(StringUtils.isBlank(phicommBuyInfo.getMemberId())){
				return PhicommConstants.FLOW_EXIT;
			}else if(null == PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())){
				return PhicommConstants.FLOW_EXIT;
			}
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
		return PhicommConstants.FLOW_SUCCESS;
	}
	
	
	public String exec(HttpConfig httpConfig , PhicommBuyInfo phicommBuyInfo, Map<String,Object> flowMap , String beforeResult){
		try{
			if(PhicommConstants.FLOW_SUCCESS.equals(beforeResult)){
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-Exec-", phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")));
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "获取验证码中" , phicommBuyInfo.getBuyType());
				File dir = new File(PhicommConstants.SAVE_VCODE_PATH);
				if(!dir.exists()){
					dir.mkdirs();
				}
				FileOutputStream fileOutputStream = new FileOutputStream(new File(PhicommConstants.SAVE_VCODE_PATH , String.format("%s.png", phicommBuyInfo.getPhone())));
				HttpClientUtil.down(httpConfig.url(String.format(String.format("%s%s", PhicommConstants.PHICOMM_HOST, "/index.php/vcode-index-passport%s.html?d=%s"),phicommBuyInfo.getMemberId() ,Math.random())).out(fileOutputStream));
				fileOutputStream.close();
				fileOutputStream = null;


				//识别验证码中
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "识别验证码中" , phicommBuyInfo.getBuyType());
				String vcode = null;
				VCode vCode = (VCode) Class.forName(PhicommConstants.VCODE_PARSE_CLASS).newInstance();
				if(null != vCode){
					String base64Image = ImageBase64Utils.imageToBase64(String.format("%s%s%s" , PhicommConstants.SAVE_VCODE_PATH , phicommBuyInfo.getPhone() , ".png"));
					vcode = vCode.vcode(base64Image);
				}
				if(StringUtils.isNotBlank(vcode)){
					phicommBuyInfo.setVcode(vcode);
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "提交斐讯校验中" , phicommBuyInfo.getBuyType());
					Map<String,Object> requestParams = new HashMap<String,Object>();
					requestParams.put("vcode" , phicommBuyInfo.getVcode());
					requestParams.put("member_id" , phicommBuyInfo.getMemberId());

					httpConfig
							.headers(HttpHeader
									.custom()
									.other("X-Requested-With", "XMLHttpRequest")
									.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
									.referer(String.format("%s/checkout-fastbuy.html", PhicommConstants.PHICOMM_HOST))
									.build())
							.map(requestParams);
					execRequestResult = HttpClientUtil.post(httpConfig.url(String.format("%s/index.php/openapi/vcodeapi/checkVcode", PhicommConstants.PHICOMM_HOST)));
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-Exec失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			if(!PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
				PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("验证码操作失败,错误信息:%s", e.getMessage()) , phicommBuyInfo.getBuyType());
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
				execRequestResult = unicodeToString(execRequestResult.trim());
				FlowUtils.info(String.format("IP:%s-手机:%s-流程:%s-After-%s-",phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name"),execRequestResult));
				if(StringUtils.isNotBlank(execRequestResult)){
					Map map = new Gson().fromJson(execRequestResult , Map.class);
					if(null != map && map.size() > 0){
						if(null != map.get("result")){
							if("failure".equals(map.get("result"))){
								execRequestResult = map.get("msg").toString();
								if(PhicommConstants.BUY_TYPE_BUY.equals(phicommBuyInfo.getBuyType()) && !isIntervalMiddle(PhicommConstants.BUY_CONTINUE_TIME, new Date())){
									beforeResult = PhicommConstants.FLOW_EXIT;
									execRequestResult += "已经超过系统连续抢购时间。";
								}
							}else if("success".equals(map.get("result"))){
								beforeResult = PhicommConstants.FLOW_BREAK;
							}
						}
					}
				}else{
					execRequestResult = "返回结果为空";
				}
				if(PhicommConstants.FLOW_BREAK.equals(beforeResult)){
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "验证码操作成功" , phicommBuyInfo.getBuyType());
				}else{
					PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), "验证码操作失败返回:" + execRequestResult , phicommBuyInfo.getBuyType());
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("IP:%s-手机:%s-流程:%s-After失败-" , phicommBuyInfo.getIp(),  phicommBuyInfo.getPhone() , flowMap.get("name")) ,e);
			if(!PhicommConstants.BUY_TYPE_BUY_CHECK.equals(phicommBuyInfo.getBuyType())){
		 		PhicommLogInfo.logInfo("put", phicommBuyInfo.getPhone(), String.format("验证码操作失败返回:%s", execRequestResult) , phicommBuyInfo.getBuyType());
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