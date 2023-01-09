package com.example.grpcserviceclientmsgframe.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class I18nLocaleConfig {
    @Bean
    I18nLocaleUtil i18nLocaleUtil() {
        return new I18nLocaleUtil();
    }

    /**
     * 国际化
     */
    @Bean(name = "messageSource")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasenames("i18n/messages");
        resourceBundleMessageSource.setCacheSeconds(3600);
        resourceBundleMessageSource.setDefaultLocale(new Locale("en_US"));
        return resourceBundleMessageSource;
    }



}
