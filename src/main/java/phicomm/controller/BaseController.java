package phicomm.controller;

import org.apache.commons.lang3.StringUtils;
import phicomm.config.PhicommConstants;

public class BaseController {


    /**
     * 功能限制
     * @param functionName
     * @return
     */
    public boolean functionIsLimit(String functionName){
        if(this.getClass().getName().contains("Controller$$")){
            String limitPrefix = this.getClass().getName().substring(0 , this.getClass().getName().indexOf("Controller$$")) + "Controller.";
            String limitFunctionName = limitPrefix + functionName;
            String limitFunctionAll = limitPrefix + "*";
            if(StringUtils.isNotBlank(PhicommConstants.LIMIT_FUNCTION)){
                String configLimitFunctionNames[] = PhicommConstants.LIMIT_FUNCTION.split(",");
                if(null != configLimitFunctionNames && configLimitFunctionNames.length > 0){
                    for(String configFunctionName : configLimitFunctionNames){
                        if(StringUtils.isNotBlank(configFunctionName) && (configFunctionName.trim().equals(limitFunctionAll) || configFunctionName.trim().equals(limitFunctionName))){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}
