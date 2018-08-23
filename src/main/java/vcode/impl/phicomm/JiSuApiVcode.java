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
import java.util.Map;

public class JiSuApiVcode implements VCode{


    @Override
    public String vcode(String base64Image) {
        String APPKEY = "123123123123";// 你的appkey
        String URL = "http://api.jisuapi.com/captcha/recognize";
        String type = "en4";// 图片类型
        String result = null;
        String url = URL + "?appkey=" + APPKEY + "&type=en4";
        Map<String, Object> param = new HashMap<String, Object>();

        try {
            param.put("pic", base64Image);
            org.apache.http.client.HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
            HttpConfig httpConfig = HttpConfig.custom().headers(HttpHeader.custom().build()).client(client);// 为每次请求创建一个实例化对象
            result = HttpClientUtil.post(httpConfig.url(url).map(param));
            if(StringUtils.isNotBlank(result)){
                Map<String,Object> map = new Gson().fromJson(result , Map.class);
                if(null != map && map.size() > 0 && null != map.get("result") && StringUtils.isNotBlank(map.get("result").toString())){
                    map = (Map<String, Object>) map.get("result");
                    if(null != map && map.size() > 0 && null != map.get("code") && StringUtils.isNotBlank(map.get("code").toString())){
                        return map.get("code").toString().trim();
                    }
                }
            }
        } catch (Exception e) {
            FlowUtils.errorException(String.format("识别验证码出错%s" ,e.getMessage()) ,e);
        }
        return null;
    }

}
