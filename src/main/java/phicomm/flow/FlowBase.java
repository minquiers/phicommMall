package phicomm.flow;

import org.apache.commons.lang3.StringUtils;

import phicomm.config.PhicommConstants;
import phicomm.logs.FlowUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlowBase {
	
	
	public String unicodeToString(String str) {
		if(StringUtils.isBlank(str)){
			return null;
		}
		String value = new String(str);
		try {
			Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
			Matcher matcher = pattern.matcher(str);
			char ch;
			while (matcher.find()) {
				ch = (char) Integer.parseInt(matcher.group(2), 16);
				str = str.replace(matcher.group(1), ch + "");
			}
			value = str;
		} catch (Exception e) {
			FlowUtils.errorException(e.getMessage(), e);
		}
		return value;
	}
	
	/**
	 * 时间是否在间隔之中
	 * @param interval
	 * @param date
	 * @return
	 */
	public static boolean isIntervalMiddle(String interval , Date date){
		if(StringUtils.isNotBlank(interval)){
			if(interval.contains("~")){
				String beginEnd[] = interval.split("~");
				if(beginEnd.length == 2){
					String begin = beginEnd[0].trim();
					String end = beginEnd[1].trim();
					if(StringUtils.isNotBlank(begin) && StringUtils.isNotBlank(end)){
						if(begin.length() == 2){
							begin += "00";
						}
						if(end.length() == 2){
							end += "00";
						}
						int currentHHMM = Integer.valueOf(new SimpleDateFormat("HHmm").format(date));
						return currentHHMM >= Integer.valueOf(begin) && currentHHMM <= Integer.valueOf(end);
					}
				}
			}
		}else{
			return true;
		}
		return false;
	}
}
