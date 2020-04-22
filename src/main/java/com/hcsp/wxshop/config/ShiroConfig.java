package com.hcsp.wxshop.config;

import com.hcsp.wxshop.service.ShiroRealm;
import com.hcsp.wxshop.service.UserContext;
import com.hcsp.wxshop.service.UserService;
import com.hcsp.wxshop.service.VerificationCodeCheckService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
public class ShiroConfig implements WebMvcConfigurer {

    private static final String COOKIE_NAME = "rememberMe"; //  cookie name

    private static final int EXPIRY_TIME = 86400; // seconds
    @Autowired
    UserService userService;

    @Value("${wxshop.redis.host}")
    String redisHost;
    @Value("${wxshop.redis.port}")
    int redisPort;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

                response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, POST, DELETE, OPTIONS");
                response.addHeader("Access-Control-Allow-Headers", "Content-Type");

                Object tel = SecurityUtils.getSubject().getPrincipal();
                if (tel != null) {
                    userService.getUserByTel(tel.toString()).ifPresent(UserContext::setCurrentUser);
                    return true;
                } else if (Arrays.asList(
                        "/api/v1/code",
                        "/api/v1/login",
                        "/api/v1/status",
                        "/api/v1/logout"
                ).contains(request.getRequestURI())) {
                    return true;
                } else {
                    response.setStatus(401);
                    return false;
                }
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                UserContext.clearCurrentUser();
            }
        });
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm, RedisCacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(shiroRealm);
        securityManager.setCacheManager(cacheManager);
        securityManager.setSessionManager(new DefaultWebSessionManager());
        securityManager.setRememberMeManager(rememberMeManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * Remember me manager
     */
    public CookieRememberMeManager rememberMeManager() {
        SimpleCookie cookie = new SimpleCookie(COOKIE_NAME);
        cookie.setMaxAge(EXPIRY_TIME);
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(cookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3KaTHGFg=="));  // RememberMe cookie encryption key default AES algorithm of key length (128, 256, 512)
        return cookieRememberMeManager;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisHost + ":" + redisPort);
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    @Bean
    public ShiroRealm myShiroRealm(VerificationCodeCheckService verificationCodeCheckService) {
        return new ShiroRealm(verificationCodeCheckService);
    }

}
