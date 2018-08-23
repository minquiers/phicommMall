package phicomm.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.Utils;
import com.google.gson.Gson;

import phicomm.config.PhicommConstants;
import util.ImageBase64Utils;

public class TensorFlowVcode {
	
	public static void main(String[] args) {
		System.out.println(vcode("2550"));;
	}
	
	public static String vcode(String name) {
		try{
			String base64 = ImageBase64Utils.imageToBase64(PhicommConstants.SAVE_VCODE_PATH + name + ".png");
			String url = "http://127.0.0.1:5000/vcode";
			Map<String, Object> reqeustParams = new HashMap<String, Object>();
			reqeustParams.put("base64", base64);
			reqeustParams.put("token", "1234567890123");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Utils.ENTITY_JSON, new Gson().toJson(reqeustParams));
			Header[] headers=HttpHeader.custom().contentType("application/json").build();
			org.apache.http.client.HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
            HttpConfig httpConfig = HttpConfig.custom().headers(headers).client(client);// 为每次请求创建一个实例化对象
			String result = HttpClientUtil.post(httpConfig.url(url).map(map));
			if(StringUtils.isNotBlank(result)){
				System.out.println(result);
				return result.trim();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "1111";
	}
	
}
