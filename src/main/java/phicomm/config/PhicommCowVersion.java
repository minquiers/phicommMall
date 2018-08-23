package phicomm.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhicommCowVersion {
	public static Map<String, String> COW_VERSION = new ConcurrentHashMap<String,String>();

	static {
		COW_VERSION.put("1", "1");
		COW_VERSION.put("4", "2");
	}
}
