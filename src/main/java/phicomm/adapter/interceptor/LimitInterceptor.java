package phicomm.adapter.interceptor;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import phicomm.config.PhicommConstants;
import logs.OtherUtils;
import phicomm.util.PhicommMessage;
import phicomm.util.RateLimiterUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 限流
 */
public class LimitInterceptor implements HandlerInterceptor{

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
		if(handler instanceof HandlerMethod && null != httpServletRequest.getParameterMap() && httpServletRequest.getParameterMap().size() > 0){
		    HandlerMethod handlerMethod = (HandlerMethod)handler;
		    String methodName = handlerMethod.getMethod().getName();
		    String ips[] = httpServletRequest.getParameterMap().get("ip");
		    if(null != ips && ips.length > 0 && StringUtils.isNotBlank(ips[0])){
		    	String ip = ips[0].trim();
		    	Integer max = null;
		    	if(StringUtils.isNotBlank(PhicommConstants.LIMIT_MAX )){
					String limitArr[] = PhicommConstants.LIMIT_MAX.split(",");
					if(null != limitArr && limitArr.length > 0){
						for(String arr : limitArr){
							String limit[] = arr.split(":");
							if(null != limit && limit.length > 0 && StringUtils.isNotBlank(limit[0]) && StringUtils.isNotBlank(limit[1])){
								if(methodName.equals(limit[0].trim())){
									max = Integer.valueOf(limit[1].trim());
								}
							}
						}
					}
				}
		    	boolean rateLimiterFalg = !RateLimiterUtils.getRateLimit(String.format("%s%s%s", ip,PhicommConstants.UNDERLINE,methodName), max);
		    	if(!rateLimiterFalg){
		    		PrintWriter printWriter = null;
		    		try{
		    			httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
		    			httpServletResponse.setCharacterEncoding("UTF-8");
		    			printWriter = httpServletResponse.getWriter();
		    			PhicommMessage phicommMessage = new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "请求频繁,请稍后再试。");
		    			printWriter.write(new Gson().toJson(phicommMessage));
		    		}catch(Exception e){
		    			OtherUtils.errorException(String.format("%s-%s-获取限流信息失败:%s",ip,methodName,e.getMessage()), e);
		    		}finally{
		    			if(null != printWriter){
		    				printWriter.close();
		    			}
		    		}
		    	}
		    	return rateLimiterFalg;
			}
		}
		return true;
	}
	
	

}
