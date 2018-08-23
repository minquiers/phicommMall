package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.ReadWriteUtil;

public class ReadExcelUtil {
	public static void main(String[] args) throws Exception{
		InputStream input = new FileInputStream(new File("c:\\a.xlsx"));  //建立输入流  
        Workbook wb  = null;  
        wb = new XSSFWorkbook(input);  
        // 获取sheet页
        Sheet sheet = wb.getSheet("Sheet1");
        StringBuilder REGION = new StringBuilder();
        StringBuilder AREA = new StringBuilder();
        StringBuilder DEALER = new StringBuilder();
        List<String> REGION_List = new ArrayList<String>();
        String REGION_TMP = "insert into ecowner.EC_PRD_REGION (REGION_ID, PRODUCT_NO, PROVINCE, CITY, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME) values (ecowner.seq_prd_region_id.nextval,'EP1700000031', '%s', '%s', null, sysdate, null, sysdate); ";
        String AREA_TMP = "insert into ecowner.ec_vwrenewal_act_area (ACT_AREA_ID, PROVINCE, PROVINCE_NAME, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME) values (ecowner.seq_ec_vwrenewal_act_area_id.nextval, '%s', '%s', null, sysdate, null, sysdate); ";
        String DEALER_TMP = "insert into ecowner.ec_vwrenewal_act_dealer (ACT_DEALER_ID, PROVINCE, DEALER_CODE, DEALER_NAME, CREATE_USER, CREATE_TIME, UPDATE_USER, UPDATE_TIME) values (ecowner.seq_ec_vwrenewal_act_dealer_id.nextval, '%s', '%s', '%s',null, sysdate,null, sysdate); ";
        
        
        for(int i = 0 ; i <= sheet.getLastRowNum();i++){
        	String cityName = sheet.getRow(i).getCell(0).getStringCellValue();
        	String dealerName = sheet.getRow(i).getCell(1).getStringCellValue();
        	String province = sheet.getRow(i).getCell(2).getStringCellValue();
        	String dealerCode = sheet.getRow(i).getCell(3).getStringCellValue();
        	if(!REGION_List.contains(province)){
	        	if(province.contains(".")){
	        		String[] s = province.split("\\.");
	        		REGION.append(String.format(REGION_TMP, s[0],s[1])).append("\r\n");
	        	}else{
	        		REGION.append(String.format(REGION_TMP, province , "ALL")).append("\r\n");
	        	}
	        	REGION_List.add(province);
	        	
	        	AREA.append(String.format(AREA_TMP, province , cityName)).append("\r\n");
        	}
        	DEALER.append(String.format(DEALER_TMP, province , dealerCode , dealerName)).append("\r\n");
        }
        ReadWriteUtil.write(REGION.toString(), "c:\\REGION.sql");
        ReadWriteUtil.write(AREA.toString(), "c:\\AREA.sql");
        ReadWriteUtil.write(DEALER.toString(), "c:\\DEALER.sql");
	}
}
