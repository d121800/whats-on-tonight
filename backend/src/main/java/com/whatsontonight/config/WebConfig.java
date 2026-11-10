package com.whatsontonight.config;

import java.util.Arrays;

import com.whatsontonight.common.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String[] corsOrigins;
    private final long maxBodyBytes;

    public WebConfig(@Value("${wot.cors-origins}") String corsOrigins,
                     @Value("${wot.max-body-bytes:4096}") long maxBodyBytes) {
        this.corsOrigins = Arrays.stream(corsOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        this.maxBodyBytes = maxBodyBytes;
    }

    /** CORS：只允許 CORS_ORIGINS 列出的前端來源。 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(corsOrigins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type")
                .maxAge(3600);
    }

    /** body 太大直接回 400，在解析 JSON 之前擋掉。 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
                if (req.getContentLengthLong() > maxBodyBytes) {
                    throw ApiException.badRequest("內容太長");
                }
                return true;
            }
        }).addPathPatterns("/api/**");
    }
}
