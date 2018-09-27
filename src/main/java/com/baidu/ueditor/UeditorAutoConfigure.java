package com.baidu.ueditor;

import com.baidu.qiniu.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截百度编辑器请求
 *
 * @author lihy
 * @version 2018/9/27
 */
@Configuration
@EnableConfigurationProperties(UdeitorProperties.class)
public class UeditorAutoConfigure extends WebMvcConfigurerAdapter {

    @Autowired
    UdeitorProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 七牛云参数赋值
        QiniuUtils.properties = this.properties;
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                ServletOutputStream out = null;
                try {
                    out = response.getOutputStream();
                    out.print(new ActionEnter(request, properties.getConfigFile()).exec());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                // false直接返回response
                return false;
            }
        }).addPathPatterns(properties.getServerUrl());
    }
}
