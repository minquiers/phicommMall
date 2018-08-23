package phicomm.core;

import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import phicomm.config.PhicommConstants;
import phicomm.flow.PhicommFlow;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;

import java.lang.reflect.Method;
import java.util.Map;

public class PhicommCouponCore extends Thread {
	private HttpClientContext context = new HttpClientContext();
	
	private PhicommBuyInfo phicommBuyInfo = new PhicommBuyInfo();
	
	public PhicommCouponCore(PhicommBuyInfo phicommBuyInfo){
		this.phicommBuyInfo = phicommBuyInfo;
	}
	
	
	
	@Override
	public void run() {
		if(null != phicommBuyInfo && null != PhicommFlow.PHICOMM_COUPON_FLOW && PhicommFlow.PHICOMM_COUPON_FLOW.size() > 0){
			FlowUtils.info(String.format("----IP:%s----手机:%s----开始获取优惠券信息---", phicommBuyInfo.getIp(), phicommBuyInfo.getPhone()));
			CookieStore cookieStore = new BasicCookieStore();
			context.setCookieStore(cookieStore);
			HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
			HttpConfig httpConfig = HttpConfig.custom().headers(HttpHeader.custom().userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36").build()).client(client).context(context);// 为每次请求创建一个实例化对象
			phicommBuyInfo.setBuyType(PhicommConstants.BUY_TYPE_COUPON);
			for (Map<String, Object> flowMap : PhicommFlow.PHICOMM_COUPON_FLOW) {
				if(null == flowMap){
					continue;
				}
				FlowUtils.info(String.format("----IP:%s----手机:%s----流程:%s-开始执行---", phicommBuyInfo.getIp(),
						phicommBuyInfo.getPhone(), flowMap.get("name")));
				String result = exec(httpConfig, flowMap);
				if(PhicommConstants.FLOW_EXIT.equals(result)){
					break;
				}
				FlowUtils.info(String.format("----IP:%s----手机:%s----流程:%s-执行完成---", phicommBuyInfo.getIp(),
						phicommBuyInfo.getPhone(), flowMap.get("name")));
			}
			FlowUtils.info(
					String.format("----IP:%s----手机:%s----结束获取优惠券信息---", phicommBuyInfo.getIp(), phicommBuyInfo.getPhone()));
		}
	}

	public String exec(HttpConfig httpConfig, Map<String, Object> flowMap) {
		String result = null;
		try {
			while (true) {
				String className = flowMap.get("name").toString();
				Object o = Class
						.forName("phicomm.flow." + className.substring(0, 1).toUpperCase() + className.substring(1)).newInstance();

				Method beforeMethod = o.getClass().getMethod("before", HttpConfig.class, PhicommBuyInfo.class, Map.class);
				result = (String) beforeMethod.invoke(o, httpConfig, phicommBuyInfo, flowMap);

				Method execMethod = o.getClass().getMethod("exec", HttpConfig.class, PhicommBuyInfo.class, Map.class,
						String.class);
				result = (String) execMethod.invoke(o, httpConfig, phicommBuyInfo, flowMap, result);

				Method method = o.getClass().getMethod("after", HttpConfig.class, PhicommBuyInfo.class, Map.class, String.class);
				result = (String) method.invoke(o, httpConfig, phicommBuyInfo, flowMap, result);

				if (PhicommConstants.FLOW_BREAK.equals(result) || PhicommConstants.FLOW_EXIT.equals(result)) {
					break;
				}else{
					Thread.sleep(Double.valueOf(flowMap.get("sleepTime").toString()).longValue());
				}
			}
		} catch (Exception e) {
			FlowUtils.errorException(String.format("%s失败",flowMap.get("name")), e);
			try{
				Thread.sleep(Double.valueOf(flowMap.get("sleepTime").toString()).longValue());
			}catch(Exception ex){
				FlowUtils.errorException(String.format("反射调用函数失败:%s" , ex) , ex);
			}
		}
		return result;
	}

}
