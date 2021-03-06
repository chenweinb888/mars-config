package com.github.fashionbrot.spring.api;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class ApiConstant {
    public static final String HEALTH = "/api/health";
    public static final String VERSION = "1.0.0";

    public static final String HTTP = "http://";

    public static final String HTTPS = "https://";

    public static final String CHECK_FOR_UPDATE_PATH ="/api/config/check-for-update";

    public static final String HTTP_CHECK_FOR_UPDATE_PATH_PARAM =HTTP+"%s"+CHECK_FOR_UPDATE_PATH;

    public static final String HTTPS_CHECK_FOR_UPDATE_PATH_PARAM =HTTPS+"%s"+CHECK_FOR_UPDATE_PATH;

    public static final String FOR_DATA ="/api/config/for-data";

    public static final String HTTP_LOAD_DATA=HTTP+"%s"+FOR_DATA;

    public static final String HTTPS_LOAD_DATA=HTTPS+"%s"+FOR_DATA;

    public static final String NAME = "mars_";
}
