package com.example.grpcserviceclientmsgframe.i18n;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class I18nLocaleUtil implements ApplicationContextAware {
    private static MessageSource messageSource;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (I18nLocaleUtil.messageSource == null) {
            I18nLocaleUtil.messageSource = applicationContext.getBean(MessageSource.class);
        }
    }


    /**
     * 翻译
     *
     * @param msg 信息
     **/
    public static String translation(String msg) {
        try {
            String message = messageSource.getMessage(msg, new Object[]{}, LocaleContextHolder.getLocale());
            if (StringUtils.isBlank(message)) {
                return msg;
            }
            return message;
        } catch (Exception e) {
            return msg;
        }

    }


}
