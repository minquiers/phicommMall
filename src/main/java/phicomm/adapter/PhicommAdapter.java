package phicomm.adapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import phicomm.adapter.interceptor.BlackListInterceptor;
import phicomm.adapter.interceptor.FunctionLimitInterceptor;
import phicomm.adapter.interceptor.LimitInterceptor;

@Configuration
public class PhicommAdapter extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则, 这里假设拦截 /url 后面的全部链接
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new BlackListInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new FunctionLimitInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new LimitInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
