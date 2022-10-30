package com.ntc.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.ntc.reggie.common.BaseContext;
import com.ntc.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // long id = Thread.currentThread().getId();
        // log.info("过滤器拦截的线程id:{}",id);
        // 利用ThreadLocal设置用户id

        // 1. 设置放行列表
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };
        String requestURI = request.getRequestURI();
        boolean result = check(urls, requestURI);


        // 匹配放行列表成功
        if (result) {
            // 直接放行
            // log.info("本次请求不需要处理{}", requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        // 用户已经登陆，可以放行
        if (request.getSession().getAttribute("employee") != null) {
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            log.info("用户已经登录");
            filterChain.doFilter(request,response);
            return;
        }
        // 如果不在放行列表且未登录则返回登陆结果，采用输出流方式写回数据
        // log.info("拦截到本次请求：{}",requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
