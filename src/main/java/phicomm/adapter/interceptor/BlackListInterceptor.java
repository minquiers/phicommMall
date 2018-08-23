package phicomm.adapter.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import logs.OtherUtils;
import phicomm.config.PhicommConstants;
import phicomm.flow.FlowBase;
import phicomm.util.PhicommBlackList;

/**
 * 黑名单Interceptor
 */
public class BlackListInterceptor implements HandlerInterceptor {
    /**
     * 在整个请求结束之后被调用
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e)
            throws Exception {
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView)
            throws Exception {

    }

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
    	//访问后台不限制
        if(handler instanceof HandlerMethod && null != httpServletRequest.getParameterMap() && httpServletRequest.getParameterMap().size() > 0){
		    HandlerMethod handlerMethod = (HandlerMethod)handler;
		    if(handlerMethod.getBean().getClass().getName().startsWith("phicomm.controller.PhicommBackendController")){
		    	return true;
		    }
		}
    	//黑名单
        boolean noLimit = true;
        String phone = null;
        if(null != httpServletRequest.getParameterMap() && httpServletRequest.getParameterMap().size() > 0){
            String ips[] = httpServletRequest.getParameterMap().get("ip");
            if(null != ips && ips.length > 0 && StringUtils.isNotBlank(ips[0])){
                String ip = ips[0].trim();
                noLimit =  isLimit(ip , httpServletResponse);
            }
            if(noLimit){
                String phones[] = httpServletRequest.getParameterMap().get("phone");
                if(null != phones && phones.length > 0 && StringUtils.isNotBlank(phones[0])){
                    phone = phones[0].trim();
                    noLimit =  isLimit(phone , httpServletResponse);
                }
            }
        }
        
        String type = "1";
        if(noLimit){
        	//开放时间限制
        	boolean isIntervalMiddle = FlowBase.isIntervalMiddle(PhicommConstants.OPEN_TIME, new Date());
        	if(!isIntervalMiddle){
        		noLimit = false;
            	type = "2";
        	}
        }
        
        
        
        if(!noLimit){
        	limit(phone, httpServletResponse , type);
        }
        return noLimit;
    }

    private boolean isLimit(String key,HttpServletResponse httpServletResponse){
        if(PhicommBlackList.PHICOMM_BLACK_MAP.containsKey(key)){
            return false;
        }
        return true;
    }
    
    private void limit(String key,HttpServletResponse httpServletResponse , String type){
    	if("1".equals(type)){
	    	if(StringUtils.isNotBlank(key)){
		    	OtherUtils.info(String.format("***************** 拦截:%s访问 *****************", key));
		        httpServletResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		        httpServletResponse.setHeader("Location", PhicommConstants.SC_MOVED_PERMANENTLY_PATH);
	    	}
    	}else{
    		httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
			httpServletResponse.setCharacterEncoding("UTF-8");
			PrintWriter printWriter;
			try {
				printWriter = httpServletResponse.getWriter();
				printWriter.write(String.format("抢购功能开放时间:%s", PhicommConstants.OPEN_TIME));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}
