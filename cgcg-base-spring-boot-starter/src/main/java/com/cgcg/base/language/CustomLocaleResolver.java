package com.cgcg.base.language;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author zhicong.lin
 */
@Configuration
public class CustomLocaleResolver  {

   @Bean
   public ReloadableResourceBundleMessageSource messageSource() {
      ReloadableResourceBundleMessageSource rs = new ReloadableResourceBundleMessageSource();
      rs.setBasename("messages");
      rs.setDefaultEncoding("UTF-8");
      rs.setUseCodeAsDefaultMessage(true);
      return rs;
   }
}