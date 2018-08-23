package phicomm.core;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import phicomm.config.PhicommConstants;
import phicomm.config.PhicommProductConfig;
import phicomm.flow.PhicommFlow;
import phicomm.logs.FlowUtils;
import logs.OtherUtils;
import phicomm.model.PhicommProduct;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PhicommSkuCore extends Thread {
	private HttpClientContext context = new HttpClientContext();

	public static void main(String[] args) {
		PhicommSkuCore phicommSkuCore = new PhicommSkuCore();
		phicommSkuCore.run();
	}


	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(PhicommConstants.SKU_QUERY_TIME);
				if(null != PhicommProductConfig.PHICOMM_PRODUCT_CONFIG && PhicommProductConfig.PHICOMM_PRODUCT_CONFIG.size() > 0){
					FlowUtils.info("----开始获取库存---");
					CookieStore cookieStore = new BasicCookieStore();
					context.setCookieStore(cookieStore);
					HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
					HttpConfig httpConfig = HttpConfig.custom().headers(HttpHeader.custom().userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36").build()).client(client).context(context);// 为每次请求创建一个实例化对象
					for (PhicommProduct phicommProduct : PhicommProductConfig.PHICOMM_PRODUCT_CONFIG) {
						if(null == phicommProduct){
							continue;
						}
						FlowUtils.info(String.format("----产品:%s%s---获取库存开始执行---", phicommProduct.getName() , phicommProduct.getColor()));
						String result = exec(httpConfig, phicommProduct);
						if(PhicommConstants.FLOW_EXIT.equals(result)){
							break;
						}
						FlowUtils.info(String.format("----产品:%s%s---获取库存执行完成---", phicommProduct.getName() , phicommProduct.getColor()));

					}
					FlowUtils.info("----获取库存结束---");
				}
			} catch (Exception e) {
				OtherUtils.errorException(String.format("获取库存失败:%s",e.getMessage()), e);
			}
		}
	}

	public String exec(HttpConfig httpConfig, PhicommProduct phicommProduct) {
		String result = null;
		Map<String,Object> flowMap = PhicommFlow.PHICOMM_QUERY_FLOW.get(0);
		try {
			while (true) {
				String className = flowMap.get("name").toString();
				Object o = Class
						.forName("phicomm.flow." + className.substring(0, 1).toUpperCase() + className.substring(1)).newInstance();

				Method beforeMethod = o.getClass().getMethod("before", HttpConfig.class, PhicommProduct.class, Map.class);
				result = (String) beforeMethod.invoke(o, httpConfig, phicommProduct, flowMap);

				Method execMethod = o.getClass().getMethod("exec", HttpConfig.class, PhicommProduct.class, Map.class,
						String.class);
				result = (String) execMethod.invoke(o, httpConfig, phicommProduct, flowMap, result);

				Method method = o.getClass().getMethod("after", HttpConfig.class, PhicommProduct.class, Map.class, String.class);
				result = (String) method.invoke(o, httpConfig, phicommProduct, flowMap, result);

				if (PhicommConstants.FLOW_BREAK.equals(result) || PhicommConstants.FLOW_EXIT.equals(result)) {
					notifyQQGroup(httpConfig ,phicommProduct);
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

	/**
	 * QQ通知
	 * @param phicommProduct
	 */
	private void notifyQQGroup(HttpConfig httpConfig,PhicommProduct phicommProduct) {
		if(PhicommConstants.SKU_NOTIFY_QQ_SWITCH.intValue() == 1
				&& 1 == phicommProduct.getSkuChangeFlag().intValue()
				&& PhicommConstants.SKU_NOTIFY_PRODUCT.contains(phicommProduct.getName())){
			Date currentDate = new Date();
			if(null == phicommProduct.getSkuNotifyDate()
					|| (currentDate.getTime() - phicommProduct.getSkuNotifyDate().getTime()) >= PhicommConstants.SKU_NOTIFY_QQ_TIME){
				try {
					Map<String,Object> notifyParams = new HashMap<String, Object>();
					notifyParams.put("key" , "1234567890123");
					notifyParams.put("msg" , phicommProduct.getSkuChangeAlertInfo());
					HttpClientUtil.post(httpConfig.url(PhicommConstants.SKU_NOTIFY_URL).map(notifyParams));
					phicommProduct.setSkuNotifyDate(new Date());
				} catch (Exception e) {
					FlowUtils.errorException(String.format("通知QQ群出错%s" , e.getMessage()) ,e);
				}
			}
		}
	}

	public static void queryAndNofitySku(){
		PhicommSkuCore phicommSkuCore = new PhicommSkuCore();
		phicommSkuCore.start();
	}

}
