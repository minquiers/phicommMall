package phicomm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.google.gson.Gson;

import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;

public class VcodeGet {
	
	public static void main2(String[] args) {
		File file =  new File("C:/Users/Administrator/Desktop/tensorflow/captcha/images/1.txt");
		file.renameTo(new File(file.getParent() + "/f.png"));
	}
	
	public static void main(String[] args) throws Exception{
		HttpClientContext context = new HttpClientContext();
		CookieStore cookieStore = new BasicCookieStore();
		context.setCookieStore(cookieStore);
		HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
		HttpConfig httpConfig = HttpConfig.custom().headers(HttpHeader.custom().build()).client(client).context(context);// 为每次请求创建一个实例化对象
		//登录
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("uname", "18688386355");
		requestParams.put("password", "qqijlnui328");
		requestParams.put("forward", "/");
		httpConfig.headers(HttpHeader.custom()
				.other("X-Requested-With", "XMLHttpRequest")
				.referer(String.format("%s/passport-login.html", PhicommConstants.PHICOMM_HOST))
				.build()).map(requestParams);
		HttpClientUtil.post(httpConfig.url(String.format("%s/passport-post_login.html", PhicommConstants.PHICOMM_HOST)));
		int success = 0;
		int error = 0;
		for(int i = 0 ; i < 2000;i++){
			File file = new File(PhicommConstants.SAVE_VCODE_PATH , String.format("%s.png", "temp"));
			//获取验证码
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			HttpClientUtil.down(httpConfig.url(String.format("%s/index.php/vcode-index-passport.html?d=", PhicommConstants.PHICOMM_HOST) + Math.random()).out(fileOutputStream));
			fileOutputStream.close();
			fileOutputStream = null;
			
			//解析验证码
			String vcode =  TensorFlowVcode.vcode("temp");
			//String vcode = "11D1";
			int autoVcodeFlag = 0;
			
			while(true){
				if(1 == autoVcodeFlag){
					System.out.println("------------success:" + success + ",------------error:" + error);
					System.out.println("请输入验证码：");
					vcode = new Scanner(System.in).nextLine().trim();
					if(1 == autoVcodeFlag){
						error += 1;
					}
					if("BREAK".equals(vcode)){
						break;
					}
				}
				
				//校验验证码
				requestParams = new HashMap<String,Object>();
				requestParams.put("vcode" , vcode);

				httpConfig
						.headers(HttpHeader
								.custom()
								.other("X-Requested-With", "XMLHttpRequest")
								.referer(String.format("%s/checkout-fastbuy.html", PhicommConstants.PHICOMM_HOST))
								.build())
						.map(requestParams);
				String execRequestResult = HttpClientUtil.post(httpConfig.url(String.format("%s/index.php/openapi/vcodeapi/checkVcode", PhicommConstants.PHICOMM_HOST)));
				execRequestResult = unicodeToString(execRequestResult.trim());
				if(StringUtils.isNotBlank(execRequestResult)){
					Map map = new Gson().fromJson(execRequestResult , Map.class);
					if(null != map && map.size() > 0){
						if(null != map.get("result")){
							if("success".equals(map.get("result"))){
								if(0 == autoVcodeFlag){
									success += 1;
								}
								break;
							}
						}
					}
					if(0 == autoVcodeFlag){
						autoVcodeFlag = 1;
					}
				}
			}
			
			//保存图片
			file.renameTo(new File(file.getParent() + "/testImages/" + vcode + UUID.randomUUID().toString() + ".png"));
			file = null;
		}
	}
	
	public static String unicodeToString(String str) {
		if(StringUtils.isBlank(str)){
			return null;
		}
		String value = new String(str);
		try {
			Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
			Matcher matcher = pattern.matcher(str);
			char ch;
			while (matcher.find()) {
				ch = (char) Integer.parseInt(matcher.group(2), 16);
				str = str.replace(matcher.group(1), ch + "");
			}
			value = str;
		} catch (Exception e) {
			FlowUtils.errorException(e.getMessage(), e);
		}
		return value;

	}
}
