package phicomm.filter;

import org.springframework.core.annotation.Order;
import phicomm.util.IPAddress;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Order(1)
@WebFilter(filterName = "testFilter1", urlPatterns = "/*")
public class GlobalFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Map<String, String[]> m = new HashMap<String, String[]>(servletRequest.getParameterMap());
        String ip = new IPAddress().getIpAddr((HttpServletRequest) servletRequest);
        String ips[] = new String[]{ip};
        m.put("ip", ips);
        servletRequest = new ParameterRequestWrapper((HttpServletRequest) servletRequest, m);
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
