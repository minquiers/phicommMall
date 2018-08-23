package phicomm.config;

public class PhicommConstants {
	//斐讯官网地址
	public static String PHICOMM_HOST = "https://www.phimall.com";
	//系统开放时间
	public static String OPEN_TIME = "";
	//验证码解析类
	public static String VCODE_PARSE_CLASS = "vcode.impl.phicomm.LocalTensorFlowVcode";
	//验证码解析地址-tensorflow
	public static String LOCAL_VCODE_PARSE_TF_PATH = "https://localhost/vcode";
	//抢购公告
	public static String BUY_NOTICE = "提前10分钟开始抢购即可。";
	//抢牛公告
	public static String DDCOW_NOTICE = "连续抢购开放时间9:50~10:10";
	//验证码存储路径
	public static String SAVE_VCODE_PATH = "/logs/vcode/";
	//前台地址
	public static String FRONT_PATH = "http://127.0.0.1:8081";
	//重定向地址
	public static String SC_MOVED_PERMANENTLY_PATH = "http://www.baidu.com/";
	//请求超时
	public static Integer REQUEST_TIME_OUT = 65000;
	//黑名单地址
	public static String BLACK_LIST_PATH = "/logs/phicomm/blackList.txt";
	//配置地址
	public static String CONFIG_LIST_PATH = "/logs/phicomm/configList.txt";
	//用户存储
	public static String USER_LIST_PATH = "/logs/phicomm/userList.txt";
	//限流-BUY-每分钟5个
	public static String LIMIT_MAX = "buy:3,playGame:3";
	//功能限制
	public static String LIMIT_FUNCTION = "";
	//限流-抢购最大线程池
	public static Integer LIMIT_THREAD_MAX = 50;
	//心跳检测超时停止
	public static Long HEART_BEAT_TIME_OUT = 180000l;
	//心跳检测时间
	public static Long HEART_BEAT_TIME = 300000l;
	//自动保存配置时间
	public static Long AUTO_SAVE_CONFIG_TIME = 600000l;
	//返利模式
	public static Boolean REBATE_MODEL = true;
	//返利手机号
	public static String REBATE_TELEPHONE = "13122165776";
	//立即支付地址
	public static String PAYMENT_PATH = "%s/checkout-dopayment-%s.html";
	//斐讯订单超时分数
	public static Integer ORDER_TIME_OUT = 30;
	//优惠券最大失败次数(超过最大使用次数跳过)
	public static Integer COUPON_USE_ERROR_MAX_COUNT = 3;
	//库存通知QQ开关,1开,其他关闭
	public static Integer SKU_NOTIFY_QQ_SWITCH = 0;
	//查看库存间隔
	public static Long SKU_QUERY_TIME = 600000l;
	//库存间隔通知时间
	public static Long SKU_NOTIFY_QQ_TIME = 8000l;
	//库存通知地址
	public static String SKU_NOTIFY_URL = "http://127.0.0.1:7101/qq";
	//库存通知产品
	public static String SKU_NOTIFY_PRODUCT = "";
	//天天牛继续购买时间
	public static String DDCOW_CONTINUE_TIME = "0950~1010";
	//商品连续抢购时间
	public static String BUY_CONTINUE_TIME = "";
	//下划线
	public static final String UNDERLINE =  "_";
	//日志-成功
	public static final String LOG_STATUS_SUCCESS = "success";
	//日志-失败
	public static final String LOG_STATUS_ERROR = "error";
	//控制层-成功
	public static final String CONTROLLER_STATUS_SUCCESS = "success";
	//控制层-失败
	public static final String CONTROLLER_STATUS_ERROR = "error";
	//流程-步骤执行成功
	public static final String FLOW_SUCCESS = "SUCCESS";
	//流程-步骤执行失败
	public static final String FLOW_CONTINUE = "CONTINUE";
	//流程-束当前流程
	public static final String FLOW_BREAK = "BREAK";
	//流程-结束所有流程
	public static final String FLOW_EXIT = "EXIT";
	//购买类型-购买
	public static final String BUY_TYPE_BUY = "BUY";
	//购买类型-预购买
	public static final String BUY_TYPE_PRE_BUY = "PRE_BUY";
	//购买类型-优惠券
	public static final String BUY_TYPE_COUPON = "COUPON";
	//购买类型-查询结算
	public static final String BUY_TYPE_QUERY_CLEARING = "QUERY_CLEARING";
	//购买类型-购买校验
	public static final String BUY_TYPE_BUY_CHECK = "BUY_CHECK";
	//购买类型-游戏
	public static final String BUY_TYPE_GAME = "GAME";
	//购买类型-游戏
	public static final String BUY_TYPE_DDCOW = "DDCOW";
	
}
