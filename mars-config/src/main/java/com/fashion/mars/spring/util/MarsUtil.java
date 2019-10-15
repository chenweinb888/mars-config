package com.fashion.mars.spring.util;



import com.fashion.mars.spring.properties.annotation.MarsIgnoreField;
import com.fashion.mars.spring.properties.annotation.MarsProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;
import static org.springframework.util.StringUtils.hasText;


@Slf4j
public class MarsUtil {

    public static <T> Class<T> resolveGenericType(Class<?> declaredClass) {
        ParameterizedType parameterizedType = (ParameterizedType) declaredClass.getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[0];
    }


   /* public static Properties loadProperties(DataConfig dataConfig, CuratorFramework zkClient){
        ServiceLoader<DynamicPropertySource> serviceLoader = ServiceLoader.load(DynamicPropertySource.class);
        List<ManagerSourceProperties> sourcePropertiesList = new ArrayList<>();
        for (DynamicPropertySource source : serviceLoader) {
            List<ManagerSourceProperties> propertiesList = source.initLoadProperties(dataConfig, zkClient);
            if (!CollectionUtils.isEmpty(propertiesList)) {
                sourcePropertiesList.addAll(propertiesList);
            }
        }
        Properties properties = new Properties();
        for (ManagerSourceProperties managerSource : sourcePropertiesList) {
            ConfigTypeEnum configType = managerSource.getDataConfig().getConfigType();
            switch (configType) {
                case PROPERTIES:
                case YAML:
                    properties.putAll(ConfigParseUtils.toProperties(managerSource.getContent(), configType.getType()));
                default:
                    break;
            }
        }
        return properties;
    }
*/


    public static PropertyValues resolvePropertyValues(Object bean, final String prefix, String appId, String envCode, String content, String type) {
        final Properties configProperties = toProperties(appId, envCode, content, type);
        final MutablePropertyValues propertyValues = new MutablePropertyValues();
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                String propertyName = resolvePropertyName(field);
                propertyName = org.springframework.util.StringUtils.isEmpty(prefix) ? propertyName : prefix + "." + propertyName;
                if (hasText(propertyName)) {
                    // If it is a map, the data will not be fetched
                    if (Collection.class.isAssignableFrom(field.getType()) ||
                            field.getType().isAssignableFrom(Map.class)) {
                        bindContainer(prefix, propertyName, configProperties, propertyValues);
                        return;
                    }
                    if (configProperties.containsKey(propertyName)) {
                        String propertyValue = configProperties.getProperty(propertyName);
                        propertyValues.add(field.getName(), propertyValue);
                    }
                }
            }
        });
        return propertyValues;
    }

    /**
     * XML configuration parsing to support different schemas
     *
     * @param dataId config dataId
     * @param group config group
     * @param text config context
     * @param type config type
     * @return {@link Properties}
     */
    public static Properties toProperties(String dataId, String group, String text, String type) {
        type = type.toLowerCase();
        return ConfigParseUtils.toProperties(dataId, group, text, type);
    }


    private static String resolvePropertyName(Field field) {
        // Ignore property name if @ManagerIgnoreField present
        if (getAnnotation(field, MarsIgnoreField.class) != null) {
            return null;
        }
        MarsProperty property = getAnnotation(field, MarsProperty.class);
        // If @ManagerProperty present ,return its value() , or field name
        return property != null ? property.value() : field.getName();
    }


    /**
     * Simple solutions to support {@link Map} or {@link Collection}
     *
     * @param fieldName property name
     * @param configProperties config context
     * @param propertyValues {@link MutablePropertyValues}
     */
    private static void bindContainer(String prefix, String fieldName, Properties configProperties, MutablePropertyValues propertyValues) {
        String regx1 = fieldName + "\\[(.*)\\]";
        String regx2 = fieldName + "\\..*";
        Pattern pattern1 = Pattern.compile(regx1);
        Pattern pattern2 = Pattern.compile(regx2);
        Enumeration<String> enumeration = (Enumeration<String>) configProperties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String s = enumeration.nextElement();
            String name = org.springframework.util.StringUtils.isEmpty(prefix) ? s : s.replace(prefix + ".", "");
            String value = configProperties.getProperty(s);
            if (configProperties.containsKey(fieldName)) {
                // for example: list=1,2,3,4,5 will be into here
                bindContainer(prefix, fieldName, listToProperties(fieldName, configProperties.getProperty(fieldName)), propertyValues);
            }
            else if (pattern1.matcher(s).find()) {
                propertyValues.add(name, value);
            } else if (pattern2.matcher(s).find()) {
                int index = s.indexOf('.');
                if (index != -1) {
                    String key = s.substring(index + 1);
                    propertyValues.add(name + "[" + key + "]", value);
                }
            }
        }
    }

    /**
     * convert list=1,2,3,4 to list[0]=1, list[1]=2, list[2]=3, list[3]=4
     *
     * @param fieldName fieldName
     * @param content content
     * @return {@link Properties}
     */
    private static Properties listToProperties(String fieldName, String content) {
        String[] splits = content.split(",");
        int index = 0;
        Properties properties = new Properties();
        for (String s : splits) {
            properties.put(fieldName + "[" + index + "]", s.trim());
            index ++;
        }
        return properties;
    }

    public static String readFromEnvironment(String label, Environment environment) {
        boolean isPlaceHolder = label.startsWith("${") && label.endsWith("}");
        if (isPlaceHolder) {
            label = label.replace("${", "").replace("}", "");
            return environment.getProperty(label);
        }
        return label;
    }


}
