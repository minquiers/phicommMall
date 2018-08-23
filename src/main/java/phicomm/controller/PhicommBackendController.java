package phicomm.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import phicomm.config.PhicommConstants;
import phicomm.config.PhicommCowVersion;
import phicomm.config.PhicommPayType;
import phicomm.config.PhicommProductConfig;
import phicomm.config.PhicommRebate;
import phicomm.flow.PhicommFlow;
import logs.OtherUtils;
import phicomm.model.PhicommBuyInfo;
import phicomm.model.PhicommConfig;
import phicomm.util.PhicommAccountList;
import phicomm.util.PhicommBlackList;
import phicomm.util.PhicommConfigList;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommMessage;
import phicomm.util.PhicommThreadPool;

@Controller
@SpringBootApplication(scanBasePackages = "phicomm")
@ServletComponentScan(basePackages = "phicomm")
@RequestMapping("/phicomm/backend")
public class PhicommBackendController extends  BaseController{

	@RequestMapping("")
	String home(@RequestParam String password) {
		if ("admin".equals(password)) {
			return "phicommBackend";
		}
		return "";
	}

	@RequestMapping("/loadCurrentUserInfo")
	String loadCurrentUserInfo() {
		return "phicommBackendCurrentUserInfo";
	}

	@RequestMapping("/loadDeaultConfig")
	@ResponseBody
	PhicommMessage loadDeaultConfig() {
		if (PhicommConfigList.PHICOMM_CONFIG_LIST == null) {
			defaultConfig();
		}
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "成功",
				PhicommConfigList.PHICOMM_CONFIG_LIST);
	}

	@RequestMapping("/removeDisable")
	@ResponseBody
	PhicommMessage removeDisable() {
		if (null != PhicommBlackList.PHICOMM_BLACK_MAP) {
			PhicommBlackList.PHICOMM_BLACK_MAP.clear();
		}
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "已经解除禁用");
	}

	@RequestMapping("/removePhone")
	@ResponseBody
	PhicommMessage removePhone(@RequestParam String phone , @RequestParam String type) {
		if(PhicommConstants.BUY_TYPE_BUY.equals(type)){
			if (null != PhicommThreadPool.PHICOMMHTREADPOOL && StringUtils.isNotBlank(phone)) {
				PhicommThreadPool.PHICOMMHTREADPOOL.remove(phone);
			}
		}else if(PhicommConstants.BUY_TYPE_GAME.equals(type)){
			if (null != PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL && StringUtils.isNotBlank(phone)) {
				PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL.remove(phone);
			}
		}else if(PhicommConstants.BUY_TYPE_DDCOW.equals(type)){
			if (null != PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL && StringUtils.isNotBlank(phone)) {
				PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.remove(phone);
			}
		}
		PhicommLogInfo.logInfo("put", phone, "已停止抢购" , null);
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "移除抢购成功");
	}

	@RequestMapping("/disabledIP")
	@ResponseBody
	PhicommMessage disabledIP(@RequestParam String limitIp) {
		if (null != PhicommBlackList.PHICOMM_BLACK_MAP) {
			PhicommBlackList.PHICOMM_BLACK_MAP.put(limitIp, "");
		}
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "禁用IP成功");
	}

	@RequestMapping("/disabledPhone")
	@ResponseBody
	PhicommMessage disabledPhone(@RequestParam String limitPhone , @RequestParam String type) {
		if (null != PhicommBlackList.PHICOMM_BLACK_MAP) {
			PhicommBlackList.PHICOMM_BLACK_MAP.put(limitPhone, "");
		}
		if(PhicommConstants.BUY_TYPE_BUY.equals(type)){
			if (null != PhicommThreadPool.PHICOMMHTREADPOOL && StringUtils.isNotBlank(limitPhone)) {
				PhicommThreadPool.PHICOMMHTREADPOOL.remove(limitPhone);
			}
		}else if(PhicommConstants.BUY_TYPE_GAME.equals(type)){
			if (null != PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL && StringUtils.isNotBlank(limitPhone)) {
				PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL.remove(limitPhone);
			}
		}else if(PhicommConstants.BUY_TYPE_DDCOW.equals(type)){
			if (null != PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL && StringUtils.isNotBlank(limitPhone)) {
				PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.remove(limitPhone);
			}
		}
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "禁用手机成功");
	}

	@RequestMapping("/removeDisabledPhone")
	@ResponseBody
	PhicommMessage removeDisabledPhone(@RequestParam String phone) {
		if (null != PhicommBlackList.PHICOMM_BLACK_MAP) {
			PhicommBlackList.PHICOMM_BLACK_MAP.remove(phone);
		}
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "移除手机成功");
	}

	@RequestMapping("/removeDisabledIP")
	@ResponseBody
	PhicommMessage removeDisabledIP(@RequestParam String limitIp) {
		if (null != PhicommBlackList.PHICOMM_BLACK_MAP) {
			PhicommBlackList.PHICOMM_BLACK_MAP.remove(limitIp);
		}
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "移除IP成功");
	}

	@RequestMapping("/currentUserInfo")
	@ResponseBody
	PhicommMessage currentUserInfo() {
		Map<String,List> map = new HashMap<String,List>();
		List<PhicommBuyInfo> phicommBuyThreadList = new ArrayList<PhicommBuyInfo>();
		List<PhicommBuyInfo> phicommGameThreadList = new ArrayList<PhicommBuyInfo>();
		List<PhicommBuyInfo> phicommGameDDCowThreadList = new ArrayList<PhicommBuyInfo>();
		PhicommMessage phicommMessage = new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "成功");
		if (null != PhicommThreadPool.PHICOMMHTREADPOOL && PhicommThreadPool.PHICOMMHTREADPOOL.size() > 0) {
			for (String phone : PhicommThreadPool.PHICOMMHTREADPOOL.keySet()) {
				phicommBuyThreadList.add(PhicommThreadPool.PHICOMMHTREADPOOL.get(phone).getPhicommBuyInfo());
			}
		}
		if (null != PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL && PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL.size() > 0) {
			for (String phone : PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL.keySet()) {
				PhicommBuyInfo phicommBuyInfo = PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL.get(phone).getPhicommBuyInfo();
				if(null != phicommBuyInfo){
					phicommBuyInfo.setFastBuyPage("-");
				}
				phicommGameThreadList.add(phicommBuyInfo);
			}
		}
		if (null != PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL && PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.size() > 0) {
			for (String phone : PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.keySet()) {
				PhicommBuyInfo phicommBuyInfo = PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.get(phone).getPhicommBuyInfo();
				if(null != phicommBuyInfo){
					phicommBuyInfo.setFastBuyPage("-");
				}
				phicommGameDDCowThreadList.add(phicommBuyInfo);
			}
		}
		phicommMessage.setResult(map);
		map.put("buy" , phicommBuyThreadList);
		map.put("game" , phicommGameThreadList);
		map.put("ddCow" , phicommGameDDCowThreadList);
		return phicommMessage;
	}

	@RequestMapping("/saveServerInfo")
	@ResponseBody
	PhicommMessage saveServerInfo() {
		PhicommMessage phicommMessage = null;
		try{
			//黑名单存储路径
			PhicommBlackList.write();
			//配置存储路径
			PhicommConfigList.write();
			//用户存储路径
			PhicommAccountList.write();
			phicommMessage = new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "服务信息写入正常,可以kill进程了");
		}catch(Exception e){
			OtherUtils.errorException(String.format("服务信息写入失败:%s",e.getMessage()), e);
			phicommMessage = new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_ERROR, String.format("保存服务信息失败,错误信息:%s", e.getMessage()));
		}
		return phicommMessage;
	}

	@RequestMapping("/updateConfig/{nofityAllUser}")
	@ResponseBody
	PhicommMessage updateConfig(@PathVariable("nofityAllUser") boolean nofityAllUser , @RequestBody List<Map<String, String>> requestParams) {
		PhicommMessage e = updateConfigAndNotifiAllUser(requestParams , nofityAllUser);
		if (e != null) return e;
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "修改成功");
	}

	private PhicommMessage updateConfigAndNotifiAllUser(@RequestBody List<Map<String, String>> requestParams , boolean notifyAllUser) {
		try {
			boolean modifiFlag = false;
			if (null != requestParams && requestParams.size() > 0) {
				for (Map<String, String> temp : requestParams) {
					if (StringUtils.isNotBlank(temp.get("name"))) {
						String name = StringUtils.isNotBlank(temp.get("name")) ? temp.get("name").trim() : null;
						String value = StringUtils.isNotBlank(temp.get("value")) ? temp.get("value").trim() : null;
						if (StringUtils.isNotBlank(name) && null != PhicommConfigList.PHICOMM_CONFIG_LIST) {
							for (PhicommConfig phicommConfig : PhicommConfigList.PHICOMM_CONFIG_LIST) {
								if (phicommConfig.getName().equals(name)) {
									if (phicommConfig.getRef() instanceof String
											|| "String".equals(phicommConfig.getType()) || null == phicommConfig.getType()) {
										phicommConfig.setRef(value);
										modifiFlag = true;
									} else if (phicommConfig.getRef() instanceof Integer
											|| "Integer".equals(phicommConfig.getType())) {
										phicommConfig.setRef(null == value ? null : Integer.valueOf(value));
										modifiFlag = true;
									} else if (phicommConfig.getRef() instanceof Long
											|| "Long".equals(phicommConfig.getType())) {
										phicommConfig.setRef(null == value ? null : Long.valueOf(value));
										modifiFlag = true;
									} else if (phicommConfig.getRef() instanceof Boolean
											|| "Boolean".equals(phicommConfig.getType())) {
										phicommConfig.setRef(null == value ? null : Boolean.valueOf(value));
										modifiFlag = true;
									} else if (phicommConfig.getRef() instanceof Map
											|| "Map".equals(phicommConfig.getType())) {
										Map<String, String> hashMap = null;
										if (null != value) {
											String mapList[] = value.split("\n");
											if (null != mapList && mapList.length > 0) {
												hashMap = new ConcurrentHashMap<String, String>();
												for (String m : mapList) {
													String mapInfo[] = m.split("\\|");
													String mapKey = StringUtils.isNotBlank(mapInfo[0])
															? mapInfo[0].trim() : "";
													String mapValue = StringUtils.isNotBlank(mapInfo[1])
															? mapInfo[1].trim() : null;
													hashMap.put(mapKey, mapValue);
												}
											}
										}
										phicommConfig.setRef(hashMap);
										modifiFlag = true;
									}else if (phicommConfig.getRef() instanceof List
											|| "List".equals(phicommConfig.getType())) {
										List<Map<String,Object>> list = null;
										if (null != value) {
											value = "[" + value + "]";
											Field field = Class.forName(phicommConfig.getRefClass()).getField(phicommConfig.getRefClassField());
											OtherUtils.info(field.toString());
											OtherUtils.info(field.getGenericType().toString());
                                            JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
                                                @Override
                                                public Date deserialize(JsonElement json, Type typeOfT,
																		JsonDeserializationContext context) throws JsonParseException {
                                                    try {
                                                        return json == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getAsString());
                                                    } catch (ParseException e) {
                                                        OtherUtils.errorException(String.format("解析时间失败:%s" , e.getMessage()) ,e);
                                                    }
                                                    return null;
                                                }
                                            };
											list = new GsonBuilder().registerTypeAdapter(Date.class , deser).create().fromJson(value, field.getGenericType());
										}
										if(null != list && list.size() > 0){
											if(null == list.get(list.size() - 1)){
												list.remove(list.size() - 1);
											}
										}
										phicommConfig.setRef(list);
										modifiFlag = true;
									}
									break;
								}
							}
						}
					}
				}
			}
			if(modifiFlag){
				//通知重新开始抢购
				if(notifyAllUser){
					if(null != PhicommThreadPool.PHICOMMHTREADPOOL && PhicommThreadPool.PHICOMMHTREADPOOL.size() > 0){
						for(String phone : PhicommThreadPool.PHICOMMHTREADPOOL.keySet()){
							PhicommLogInfo.logInfo("put", phone, "抢购网站升级了请重新点击[开始抢购]按钮" , PhicommConstants.BUY_TYPE_BUY);
						}
					}
					if(null != PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL && PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL.size() > 0){
						for(String phone : PhicommThreadPool.PHICOMM_GAME_HTREAD_POOL.keySet()){
							PhicommLogInfo.logInfo("put", phone, "抢购网站升级了请重新点击[开始抢购]按钮" , PhicommConstants.BUY_TYPE_GAME);
						}
					}
					if(null != PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL && PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.size() > 0){
						for(String phone : PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.keySet()){
							PhicommLogInfo.logInfo("put", phone, "抢购网站升级了请重新点击[开始抢购]按钮" , PhicommConstants.BUY_TYPE_DDCOW);
						}
					}
				}
				saveServerInfo();
			}
		} catch (Exception e) {
			OtherUtils.errorException(String.format("修改失败,错误信息:%s", e.getMessage()) ,e);
			return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_ERROR,
					String.format("修改失败,错误信息:%s", e.getMessage()));
		}
		return null;
	}

	/**
	 * 默认配置载入
	 */
	public void defaultConfig() {
		PhicommConfigList.init();
		PhicommConfig phicommHost = new PhicommConfig(null, "phicommHost", "斐讯官网地址",
				PhicommConstants.class.getName(), "PHICOMM_HOST");
		PhicommConfig openTime = new PhicommConfig(null, "openTime", "抢购开放时间",
				PhicommConstants.class.getName(), "OPEN_TIME");
		PhicommConfig vcodeSavePath = new PhicommConfig(null, "vcodeSavePath", "验证码存储路径",
				PhicommConstants.class.getName(), "SAVE_VCODE_PATH");
		PhicommConfig vcodeParseClass = new PhicommConfig(null, "vcodeParseClass", "验证码解析类",
				PhicommConstants.class.getName(), "VCODE_PARSE_CLASS");
		PhicommConfig tfVcodeParsePath = new PhicommConfig(null, "tfVcodeParsePath", "tf验证码解析地址",
				PhicommConstants.class.getName(), "LOCAL_VCODE_PARSE_TF_PATH");
		PhicommConfig frontPath = new PhicommConfig(null, "frontPath", "前台地址",
				PhicommConstants.class.getName(), "FRONT_PATH");
		PhicommConfig buyNotice = new PhicommConfig(null, "buyNotice", "抢购页面公告",
				PhicommConstants.class.getName(), "BUY_NOTICE");
		PhicommConfig ddCowNotice = new PhicommConfig(null, "ddCowNotice", "抢牛页面公告",
				PhicommConstants.class.getName(), "DDCOW_NOTICE");
		PhicommConfig skuNotifyQQSwitch = new PhicommConfig("(1开,其他关闭)", "skuNotifyQQSwitch", "库存QQ通知开关",
				PhicommConstants.class.getName(), "SKU_NOTIFY_QQ_SWITCH");
		PhicommConfig skuQueryTime = new PhicommConfig("(毫秒)", "skuQueryTime", "查看库存间隔",
				PhicommConstants.class.getName(), "SKU_QUERY_TIME");
		PhicommConfig skuNofityQQTime = new PhicommConfig("(毫秒)", "skuNofityQQTime", "库存通知QQ间隔",
				PhicommConstants.class.getName(), "SKU_NOTIFY_QQ_TIME");
		PhicommConfig functionLimit = new PhicommConfig(null, "functionLimit", "功能限制",
				PhicommConstants.class.getName(), "LIMIT_FUNCTION");
		PhicommConfig skuNotifyUrl = new PhicommConfig(null, "skuNotifyUrl", "库存通知地址",
				PhicommConstants.class.getName(), "SKU_NOTIFY_URL");
		PhicommConfig skuNotifyProduct = new PhicommConfig(null, "skuNotifyProduct", "库存需要QQ通知产品",
				PhicommConstants.class.getName(), "SKU_NOTIFY_PRODUCT");
		PhicommConfig movedPermanentlyPath = new PhicommConfig(null, "movedPermanentlyPath", "重定向地址",
				PhicommConstants.class.getName(), "SC_MOVED_PERMANENTLY_PATH");
		PhicommConfig requestTimeOut = new PhicommConfig("(毫秒)", "requestTimeOut", "请求超时(毫秒)",
				PhicommConstants.class.getName(), "REQUEST_TIME_OUT");
		PhicommConfig blackListPath = new PhicommConfig(null, "blackListPath", "黑名单存储路径",
				PhicommConstants.class.getName(), "BLACK_LIST_PATH");
		PhicommConfig configListPath = new PhicommConfig(null, "configListPath", "配置存储路径",
				PhicommConstants.class.getName(), "CONFIG_LIST_PATH");
		PhicommConfig userListPath = new PhicommConfig(null, "userListPath", "用户存储路径", PhicommConstants.class.getName(),
				"USER_LIST_PATH");
		PhicommConfig limitMax = new PhicommConfig("个", "limitMax", "函数限流(每分钟/次)",
				PhicommConstants.class.getName(), "LIMIT_MAX");
		PhicommConfig limitThreadMax = new PhicommConfig("个", "limitThreadMax", "抢购最大线程池",
				PhicommConstants.class.getName(), "LIMIT_THREAD_MAX");
		PhicommConfig heartBeatTimeOut = new PhicommConfig("(毫秒)", "heartBeatTimeOut", "心跳检测超时时间(毫秒)",
				PhicommConstants.class.getName(), "HEART_BEAT_TIME_OUT");
		PhicommConfig heartBeatTime = new PhicommConfig("(毫秒)", "heartBeatTime", "心跳检测时间(毫秒)",
				PhicommConstants.class.getName(), "HEART_BEAT_TIME");
		PhicommConfig autoSaveConfigTime = new PhicommConfig("(毫秒)", "autoSaveConfigTime", "自动保存配置时间(毫秒)",
				PhicommConstants.class.getName(), "AUTO_SAVE_CONFIG_TIME");
		PhicommConfig orderTimeOut = new PhicommConfig("(分钟)", "orderTimeOut", "斐讯订单超时(分数)",
				PhicommConstants.class.getName(), "ORDER_TIME_OUT");
		PhicommConfig rebateModel = new PhicommConfig(null, "rebateModel", "返利开关", PhicommConstants.class.getName(),
				"REBATE_MODEL");
		PhicommConfig notRebate = new PhicommConfig(null, "notRebate", "开启返利模式但是不返利产品", PhicommRebate.class.getName(),
				"PRODUCT_NOT_REBATE");
		PhicommConfig rebateTelephone = new PhicommConfig(null, "rebateTelephone", "返利手机号",
				PhicommConstants.class.getName(), "REBATE_TELEPHONE");
		PhicommConfig product = new PhicommConfig(null, "product", "产品信息(产品名称|产品购买地址)", PhicommProductConfig.class.getName(),
				"PHICOMM_PRODUCT_CONFIG");
		PhicommConfig rebate = new PhicommConfig(null, "rebate", "返利信息(手机号|返利ID)", PhicommRebate.class.getName(),
				"PHICOMM_ACCOUNT_REBATE_MAP");
		PhicommConfig paymentPath = new PhicommConfig(null, "paymentPath", "斐讯付款地址", PhicommConstants.class.getName(),
				"PAYMENT_PATH");
		PhicommConfig payType = new PhicommConfig(null, "payType", "斐讯付款方式", PhicommPayType.class.getName(),
				"PAY_TYPE");
		PhicommConfig cowVersion = new PhicommConfig(null, "cowVersion", "牛版本", PhicommCowVersion.class.getName(),
				"COW_VERSION");
		PhicommConfig ddCowContinueTime = new PhicommConfig(null, "ddCowContinueTime", "天天牛继续购买时间", PhicommConstants.class.getName(),
				"DDCOW_CONTINUE_TIME");
		PhicommConfig buyContinueTime = new PhicommConfig(null, "buyContinueTime", "商品连续抢购时间", PhicommConstants.class.getName(),
				"BUY_CONTINUE_TIME");
		PhicommConfig couponUseMaxErrorCount = new PhicommConfig(null, "couponUseMaxErrorCount", "优惠券最大失败次数(超过最大使用次数跳过)", PhicommConstants.class.getName(),
				"COUPON_USE_ERROR_MAX_COUNT");
		PhicommConfig buyflow = new PhicommConfig(null, "buyflow", "购买流程", PhicommFlow.class.getName(),
				"PHICOMM_BUY_FLOW");
		PhicommConfig buyCheckflow = new PhicommConfig(null, "buyCheckflow", "购买校验流程", PhicommFlow.class.getName(),
				"PHICOMM_BUY_CHECK_FLOW");
		PhicommConfig couponflow = new PhicommConfig(null, "couponflow", "优惠券流程", PhicommFlow.class.getName(),
				"PHICOMM_COUPON_FLOW");
		PhicommConfig querySku = new PhicommConfig(null, "querySku", "查看库存流程", PhicommFlow.class.getName(),
				"PHICOMM_QUERY_FLOW");
		PhicommConfig gameFlow = new PhicommConfig(null, "gameFlow", "橙子游戏流程", PhicommFlow.class.getName(),
				"PHICOMM_GAME_FLOW");
		PhicommConfig ddCowFlow = new PhicommConfig(null, "ddCowFlow", "天天牛流程", PhicommFlow.class.getName(),
				"PHICOMM_GAMEDDCOW_FLOW");

		PhicommConfigList.PHICOMM_CONFIG_LIST.add(phicommHost);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(openTime);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(buyNotice);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(ddCowNotice);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(vcodeSavePath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(vcodeParseClass);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(tfVcodeParsePath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(frontPath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(skuNotifyQQSwitch);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(skuQueryTime);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(skuNofityQQTime);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(functionLimit);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(skuNotifyUrl);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(skuNotifyProduct);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(movedPermanentlyPath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(requestTimeOut);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(limitMax);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(limitThreadMax);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(heartBeatTime);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(heartBeatTimeOut);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(autoSaveConfigTime);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(orderTimeOut);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(rebateModel);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(notRebate);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(couponUseMaxErrorCount);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(paymentPath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(payType);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(cowVersion);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(ddCowContinueTime);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(buyContinueTime);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(product);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(rebate);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(rebateTelephone);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(blackListPath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(configListPath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(userListPath);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(buyflow);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(buyCheckflow);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(couponflow);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(querySku);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(gameFlow);
		PhicommConfigList.PHICOMM_CONFIG_LIST.add(ddCowFlow);
	}

	public static void main(String[] args) {
		//用户存储路径
		PhicommAccountList.load();
		//黑名单存储路径
		PhicommBlackList.load();
		//配置存储路径
		PhicommConfigList.load();
		SpringApplication.run(PhicommBackendController.class, args);
	}

}
