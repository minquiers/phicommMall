package phicomm.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhicommPayType {
	public static Map<String,String> PAY_TYPE = new ConcurrentHashMap<String,String>();
	
	static{
		PAY_TYPE.put("银联在线", "newchinapay");
		PAY_TYPE.put("支付宝", "alipay");
		PAY_TYPE.put("微信", "wxpay");
		//PAY_TYPE.put("京东支付", "jdpay");
	}
}
