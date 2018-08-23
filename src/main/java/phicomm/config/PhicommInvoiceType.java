package phicomm.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhicommInvoiceType {
	public static Map<String, String> INVOICE_TYPE = new ConcurrentHashMap<String,String>();

	static {
		INVOICE_TYPE.put("公司", "1");
		INVOICE_TYPE.put("个人", "0");
	}
}
