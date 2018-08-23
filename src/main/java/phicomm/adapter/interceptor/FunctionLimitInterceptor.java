package phicomm.adapter.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import logs.OtherUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 功能限制
 */
public class FunctionLimitInterceptor implements HandlerInterceptor {

	/**
	 * 在整个请求结束之后被调用
	 */
	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
	}

	/**
	 * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
	 */
	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {

	}

	/**
	 * 在请求处理之前进行调用（Controller方法调用之前）
	 */
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler) throws Exception {
		if (handler instanceof HandlerMethod && null != httpServletRequest.getParameterMap()
				&& httpServletRequest.getParameterMap().size() > 0) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			String methodName = handlerMethod.getMethod().getName();

			PrintWriter printWriter = null;
			try {
				if (handlerMethod.getBean().getClass().getPackage().getName().startsWith("phicomm.")) {
					Boolean functionLimitFlag = (Boolean) handlerMethod.getBean().getClass()
							.getMethod("functionIsLimit", String.class).invoke(handlerMethod.getBean(), methodName);
					if (null != functionLimitFlag && functionLimitFlag.booleanValue()) {
						httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
						httpServletResponse.setCharacterEncoding("UTF-8");
						printWriter = httpServletResponse.getWriter();
						printWriter.write("此功能暂时关闭。");
						return false;
					}
				}
			} catch (Exception e) {
				OtherUtils.errorException(String.format("-%s-获取功能限制信息失败:%s", methodName, e.getMessage()), e);
			} finally {
				if (null != printWriter) {
					printWriter.close();
				}
			}
		}
		return true;
	}

}
