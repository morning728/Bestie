package com.morning.mailnotification.config;


import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactory(DataSource dataSource) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));  // Указываем путь к файлу конфигурации Quartz
        schedulerFactoryBean.setJobFactory(new AutowiringSpringBeanJobFactory());
        return schedulerFactoryBean;
    }

    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

        private ApplicationContext context;

        @Override
        public void setApplicationContext(ApplicationContext context) {
            this.context = context;
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            context.getAutowireCapableBeanFactory().autowireBean(job);
            return job;
        }
    }
}

