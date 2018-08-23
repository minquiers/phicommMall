package phicomm.old;

import phicomm.core.PhicommGameCore;
import phicomm.logs.FlowUtils;
import phicomm.model.PhicommBuyInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoOrange {
	public static void main(String[] args) throws Exception{
		while(true){
			Date currentDate = new Date();
			String currentTimeHH = new SimpleDateFormat("yyyy-MM-dd HH").format(currentDate);
			String currentTimeMMSS = new SimpleDateFormat("HH:mm:ss").format(currentDate);
			String currentTimeSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate);
			if(currentTimeHH.equals("2018-03-01 00")){
				break;
			}
			
			if(currentTimeMMSS.equals("12:00:00") || (args != null && args.length > 0)){
				args = null;
				for(String key : PhicommAccount.PHICOMM_ACCOUNT.keySet()){
					try{
						String value = PhicommAccount.PHICOMM_ACCOUNT.get(key);
						PhicommBuyInfo phicommBuyInfo = new PhicommBuyInfo();
						phicommBuyInfo.setPhone(key);
						phicommBuyInfo.setPassword(value);
						phicommBuyInfo.setNeedInvoice(true);
						phicommBuyInfo.setInvoiceType("0");
						phicommBuyInfo.setPayAppId("jdpay");
						phicommBuyInfo.setNumber(1);
						phicommBuyInfo.setIp("192.168.1.1");
						phicommBuyInfo.setFastBuyPage("index.php/cart-fastbuy-5-1.html");
						phicommBuyInfo.setDlyTypeId("1");
						PhicommGameCore phicommGameCore = new PhicommGameCore(phicommBuyInfo);
						phicommGameCore.run();
					}catch(Exception e){
						FlowUtils.errorException(e.getMessage(), e);
					}
				}
			}else{
				FlowUtils.info(String.format("还没开始当前时间%s", currentTimeSS));
			}
			
			Thread.sleep(1000l);
		}
	}
}
