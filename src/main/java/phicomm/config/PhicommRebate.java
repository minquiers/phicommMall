package phicomm.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 返利
 * @author Administrator
 *
 */
public class PhicommRebate {

	public static Map<String,String> PHICOMM_ACCOUNT_REBATE_MAP = new ConcurrentHashMap<String,String>();
	public static List<String> PRODUCT_NOT_REBATE = new ArrayList<String>();
	static{
		PHICOMM_ACCOUNT_REBATE_MAP.put("13122165776", "32295347311");  //13122165776推广
	}
	
}
