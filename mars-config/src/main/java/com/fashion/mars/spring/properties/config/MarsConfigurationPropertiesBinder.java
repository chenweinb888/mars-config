package com.fashion.mars.spring.properties.config;

import com.fashion.mars.spring.properties.annotation.MarsConfigurationProperties;
import com.fashion.mars.spring.util.MarsUtil;
import com.fashion.mars.spring.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyValues;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Slf4j
public class MarsConfigurationPropertiesBinder {

    public static final String BEAN_NAME = "ManagerConfigurationPropertiesBinder";


    private final ConfigurableApplicationContext applicationContext;

    private final Environment environment;

    private final ApplicationEventPublisher applicationEventPublisher;


    public MarsConfigurationPropertiesBinder(ConfigurableApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ConfigurableApplicationContext must not be null!");
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
        this.applicationEventPublisher = applicationContext;
    }

    protected void bind(Object bean, String beanName) {

        MarsConfigurationProperties properties = findAnnotation(bean.getClass(), MarsConfigurationProperties.class);

        bind(bean, beanName, properties);

    }

    public void bind(final Object bean, final String beanName, final MarsConfigurationProperties properties) {

        Assert.notNull(bean, "Bean must not be null!");

        Assert.notNull(properties, "ManagerConfigurationProperties must not be null!");


        /*ForDataVo forDataVo = FileCache.getCache(properties.fileName());
        if (forDataVo !=null){
            String content = forDataVo.getContent();
            String appId = forDataVo.getAppId();
            String envCode = forDataVo.getEnvCode();
            if (hasText(content)) {
                doBind(bean, beanName,appId, envCode, properties, content);
            }
        }*/
    }

    protected void doBind(Object bean, String beanName, String appId, String envCode,MarsConfigurationProperties properties, String content) {
        String type = properties.type().getType();
        String prefix = properties.prefix();

        PropertyValues propertyValues = MarsUtil.resolvePropertyValues(bean, prefix, appId, envCode, content, type);
        doBind(bean, properties, propertyValues);

    }

    private void doBind(Object bean, MarsConfigurationProperties properties,
                        PropertyValues propertyValues) {
        ObjectUtils.cleanMapOrCollectionField(bean);
        DataBinder dataBinder = new DataBinder(bean);
        dataBinder.setIgnoreInvalidFields(properties.ignoreInvalidFields());
        dataBinder.setIgnoreUnknownFields(properties.ignoreUnknownFields());
        /*dataBinder.setAutoGrowNestedPaths(properties.ignoreNestedProperties());
        dataBinder.setIgnoreInvalidFields(properties.ignoreInvalidFields());
        dataBinder.setIgnoreUnknownFields(properties.ignoreUnknownFields());*/
        dataBinder.bind(propertyValues);
    }

}
