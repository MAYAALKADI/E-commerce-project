package com.example.store_project.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagConfig {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> etagFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> reg =
                new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        reg.addUrlPatterns("/api/v1/products", "/api/v1/products/*");
        reg.setName("etagFilter");
        return reg;
    }
}