package vcode.impl.phicomm;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import vcode.VCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaiduVcode implements VCode{
	

	@Override
	public String vcode(String base64Image) {
		try{
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("image", base64Image);
			map.put("detect_direction", false);
			map.put("detect_language", false);
			String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/webimage?access_token=1111";
			org.apache.http.client.HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
            HttpConfig httpConfig = HttpConfig.custom().headers(HttpHeader.custom().build()).client(client);// 为每次请求创建一个实例化对象
			String result = HttpClientUtil.post(httpConfig.url(url).map(map));
			if(StringUtils.isNotBlank(result)){
				Map<String,Object> m = new Gson().fromJson(result, Map.class);
				if(null != m && m.size() > 0){
					Object o = m.get("words_result");
					if(null != o){
						List<Map<String,Object>> list = (List<Map<String, Object>>) o;
						if(list.size() > 0){
							if(null != list.get(0) && list.get(0).size() > 0 && null != list.get(0).get("words")){
								return (String) list.get(0).get("words");
							}
						}
					}
				}
			}
		}catch(Exception e){
			FlowUtils.errorException(String.format("识别验证码出错%s" ,e.getMessage()) ,e);
		}
		return null;
	}
	
}
