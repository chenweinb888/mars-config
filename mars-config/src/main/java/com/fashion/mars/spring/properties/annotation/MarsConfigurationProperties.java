package com.fashion.mars.spring.properties.annotation;

import com.fashion.mars.spring.enums.ConfigTypeEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MarsConfigurationProperties {

    /**
     * appManagerCenter fileName
     */
    String fileName();

    /**
     * config prefix name
     *
     * @return default value is <code>""</code>
     */
    String prefix() default "";


    /**
     * It indicates the properties of current doBind bean is auto-refreshed when  configuration is changed.
     *
     * @return default value is <code>false</code>
     */
    boolean autoRefreshed() default false;


    /**
     * config style
     *
     * @return default value is {@link ConfigTypeEnum#PROPERTIES}
     */
    ConfigTypeEnum type() default ConfigTypeEnum.PROPERTIES;


    /**
     * Flag to indicate that when binding to this object invalid fields should be ignored.
     * Invalid means invalid according to the binder that is used, and usually this means
     * fields of the wrong type (or that cannot be coerced into the correct type).
     * @return the flag value (default false)
     */
    boolean ignoreInvalidFields() default false;

    /**
     * Flag to indicate that when binding to this object unknown fields should be ignored.
     * An unknown field could be a sign of a mistake in the Properties.
     * @return the flag value (default true)
     */
    boolean ignoreUnknownFields() default true;
}