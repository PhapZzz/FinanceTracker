package com.financetracker.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/reports/**")
                .addResourceLocations("file:/absolute/path/to/uploads/reports/");
        // Nếu bạn lưu ở nơi khác thì sửa đường dẫn tại đây
    }
}
