package com.fashion.mars.spring.env;

import com.alibaba.fastjson.JSONObject;
import com.fashion.mars.ribbon.loadbalancer.BaseLoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.ILoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.Server;
import com.fashion.mars.spring.config.GlobalMarsProperties;
import com.fashion.mars.spring.server.ServerHttpAgent;
import com.fashion.mars.spring.util.BeanUtil;
import com.fashion.mars.spring.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;


/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Slf4j
public class MarsPropertySourcePostProcessor implements BeanDefinitionRegistryPostProcessor, BeanFactoryPostProcessor,
        EnvironmentAware, Ordered, DisposableBean {

    public static final String BEAN_NAME = "marsPropertySourcePostProcessor";

    private ConfigurableEnvironment environment;


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {


        GlobalMarsProperties globalMarsProperties = (GlobalMarsProperties) BeanUtil.getSingleton(beanFactory, GlobalMarsProperties.BEAN_NAME);
        if (globalMarsProperties == null) {
            log.warn("globalMarsProperties is null");
            return;
        }
        String appId = globalMarsProperties.getAppName();
        String envCode = globalMarsProperties.getEnvCode();
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(envCode)) {
            if (log.isInfoEnabled()) {
                log.info(" mars init appId is null or envCode is null");
            }
            return;
        }
        String serverAddress = globalMarsProperties.getServerAddress();
        if (StringUtil.isEmpty(serverAddress)) {
            log.warn(" ${mars.config.http.server-address} is null");
            return;
        }
        ILoadBalancer loadBalancer = new BaseLoadBalancer();
        ServerHttpAgent.setServer(serverAddress, loadBalancer);
        Server server = loadBalancer.chooseServer();
        if (server == null) {
            log.error("MarsPropertySourcePostProcessor next server is null serverList:" + JSONObject.toJSONString(loadBalancer.getAllServers()));
            return;
        }
        ServerHttpAgent.checkForUpdate(server, globalMarsProperties, environment);

    }


    /**
     * The order is closed to {@link ConfigurationClassPostProcessor#getOrder() HIGHEST_PRECEDENCE} almost.
     *
     * @return <code>Ordered.HIGHEST_PRECEDENCE + 1</code>
     * @see ConfigurationClassPostProcessor#getOrder()
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }


    @Override
    public void destroy() throws Exception {
        ServerHttpAgent.shutdown();
    }
}