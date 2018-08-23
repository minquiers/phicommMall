package vcode.impl.phicomm;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.Utils;
import com.google.gson.Gson;
import logs.OtherUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;
import util.ImageBase64Utils;
import vcode.VCode;

import java.util.HashMap;
import java.util.Map;

public class LocalTensorFlowVcode implements VCode {

    @Override
    public String vcode(String base64Image) {
        try {
            String url = PhicommConstants.LOCAL_VCODE_PARSE_TF_PATH;
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("token", "1234567890123");
            param.put("base64", base64Image);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(Utils.ENTITY_JSON, new Gson().toJson(param));
            Header[] headers=HttpHeader.custom().contentType("application/json").build();
            org.apache.http.client.HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
            HttpConfig httpConfig = HttpConfig.custom().headers(headers).client(client);// 为每次请求创建一个实例化对象
            String result = HttpClientUtil.post(httpConfig.url(url).map(map));
            if(StringUtils.isNotBlank(result)){
                return result.trim();
            }
        } catch (Exception e) {
            FlowUtils.errorException(String.format("识别验证码出错%s" ,e.getMessage()) ,e);
        }
        return null;
    }

    public static void main(String args[]) throws Exception{
        String base64 = ImageBase64Utils.imageToBase64("mages\\0A0Cb40205af-dbcd-4d47-8436-44da3a171406.png");
        VCode vCode = new LocalTensorFlowVcode();
        OtherUtils.info(String.format("验证码为:%s" , vCode.vcode(base64)));
    }

}
