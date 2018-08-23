package phicomm.core;

import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.client.HttpClient;
import phicomm.config.PhicommConstants;
import phicomm.flow.PhicommFlow;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommThreadPool;

import java.lang.reflect.Method;
import java.util.Map;

public class PhicommFastBuyCheck extends Thread {
	
	private String phone = null;
	private PhicommBuyInfo phicommBuyInfo = new PhicommBuyInfo();
	
	public PhicommFastBuyCheck(String phone){
		this.phone = phone;
	}
	
	@Override
	public void run() {
		PhicommFastBuyCore phicommFastBuyCore = PhicommThreadPool.PHICOMMHTREADPOOL.get(phone);
		try {
			BeanUtils.copyProperties(phicommBuyInfo, phicommFastBuyCore.getPhicommBuyInfo());
		} catch (Exception e) {
			phicommBuyInfo = null;
			FlowUtils.errorException(e.getMessage(), e);
		} 
		if(null != phicommFastBuyCore && null != phicommBuyInfo && null != PhicommFlow.PHICOMM_BUY_CHECK_FLOW && PhicommFlow.PHICOMM_BUY_CHECK_FLOW.size() > 0){
			FlowUtils.info(
					String.format("----IP:%s----手机:%s----开始抢购校验---", phicommBuyInfo.getIp(), phicommBuyInfo.getPhone()));
			HttpClient client = HCB.custom().timeout(PhicommConstants.REQUEST_TIME_OUT).build();// 最多创建20个http链接
			HttpConfig httpConfig = HttpConfig.custom().headers(HttpHeader.custom().userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36").build()).client(client).context(phicommFastBuyCore.getContext());// 为每次请求创建一个实例化对象
			phicommBuyInfo.setBuyType(PhicommConstants.BUY_TYPE_BUY_CHECK);
			for (Map<String, Object> flowMap : PhicommFlow.PHICOMM_BUY_CHECK_FLOW) {
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
			PhicommThreadPool.PHICOMMHTREADPOOL.remove(phicommBuyInfo.getPhone());
			FlowUtils.info(
					String.format("----IP:%s----手机:%s----结束抢购校验---", phicommBuyInfo.getIp(), phicommBuyInfo.getPhone()));
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
