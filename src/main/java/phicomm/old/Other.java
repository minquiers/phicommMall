package phicomm.old;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import phicomm.logs.FlowUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Other {
	public static void main(String[] args) throws Exception{
		FlowUtils.info("手机号：");
		for(int i = 0 ; i < PhicommAccount.PHICOMM_ACCOUNT.keySet().toArray().length;i++){
			FlowUtils.info((i + 1) + ":" + PhicommAccount.PHICOMM_ACCOUNT.keySet().toArray()[i]);
		}
		String phone = PhicommAccount.PHICOMM_ACCOUNT.keySet().toArray()[new Scanner(System.in).nextInt() - 1].toString();
		String password = PhicommAccount.PHICOMM_ACCOUNT.get(phone);
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		
		
		Map<String,Object> requestParams = new HashMap<String,Object>();
		requestParams.put("phone", phone);
		requestParams.put("password", new BigInteger(1, md.digest()).toString(16));
		requestParams.put("verify", "");
		requestParams.put("first_login", "");
		HttpConfig httpConfig = HttpConfig.custom()
				.headers(HttpHeader
						.custom()
						.accept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89")
						.other("channel-type", "mallapp_ios")
						.acceptEncoding("gzip, deflate")
						.connection("keep-alive")
						.host("mall.phicomm.com")
						.build())
				.url("wap.php?m=wap&c=login&a=login").map(requestParams);
		checkResponseResult(HttpClientUtil.post(httpConfig), "ok");
	
		requestParams = new HashMap<String,Object>();
		requestParams.put("score", 330);
		httpConfig = HttpConfig.custom()
				.headers(HttpHeader
						.custom()
						.accept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89")
						.other("channel-type", "mallapp_ios")
						.acceptEncoding("gzip, deflate")
						.connection("keep-alive")
						.host("mall.phicomm.com")
						.build())
				.url("appmall.php?m=appmall&c=accelerator&a=recordgame").map(requestParams);
		HttpClientUtil.post(httpConfig);
	}
	
	/**
	 * 检测校验结果
	 * @param responseStr
	 * @param successFlag
	 * @return
	 */
	public static Map<String,Object> checkResponseResult(String responseStr , String successFlag){
		if(StringUtils.isNoneBlank(responseStr) && StringUtils.isNoneBlank(successFlag)){
			Map<String,Object> response = new Gson().fromJson(responseStr, Map.class);
			if(null != response && null != response.get("status") && successFlag.trim().equals(response.get("status").toString().trim())){
				return response;
			}
		}
		throw new RuntimeException("返回错误！");
	}
}
