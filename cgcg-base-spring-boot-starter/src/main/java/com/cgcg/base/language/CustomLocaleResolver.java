package com.cgcg.base.language;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

   @Bean
   public ResourceBundleMessageSource messageSource() {
      ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
      rs.setBasename("messages");
      rs.setDefaultEncoding("UTF-8");
      rs.setUseCodeAsDefaultMessage(true);
      return rs;
   }
}