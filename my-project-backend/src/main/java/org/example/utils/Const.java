package org.example.utils;

public class Const {
    //JWT令牌
    public final static String JWT_BLACK_LIST = "jwt:blacklist:";
    public final static String JWT_FREQUENCY = "jwt:frequency:";
    //邮件验证
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit";
    public static final String VERIFY_EMAIL_DATA = "verify:password:data";
    //过滤器优先级
    public final static int ORDER_FLOW_LIMIT = -101;
    public final static int ORDER_CORS = -102;
    //请求频率限制
    public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";
    //用户角色
    public static final String ROLE_DEFAULT = "user";
    public static final String ATTR_USER_ID = "userId";
    //论坛相关
    public static final String FORUM_WEATHER_CACHE = "weather:cache:";
    public static final String FORUM_IMAGE_COUNTER = "forum:image:";
    public static final String FORUM_TOPIC_CREATE_COUNTER = "forum:topic:create:";
    public static final String FORUM_TOPIC_COMMENT_COUNTER = "forum:topic:comment:";
    public static final String FORUM_TOPIC_PREVIEW_CACHE = "topic:preview:";

}
