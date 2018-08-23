package phicomm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import phicomm.config.PhicommConstants;
import phicomm.config.PhicommPayType;
import phicomm.config.PhicommProductConfig;
import phicomm.config.PhicommRebate;
import phicomm.core.*;
import phicomm.model.PhicommBuyHomeInfo;
import phicomm.model.PhicommBuyInfo;
import phicomm.model.PhicommProduct;
import phicomm.util.PhicommAccountList;
import phicomm.util.PhicommBlackList;
import phicomm.util.PhicommConfigList;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommMessage;
import phicomm.util.PhicommThreadPool;

@Controller
@SpringBootApplication(scanBasePackages = "phicomm")
@ServletComponentScan(basePackages = "phicomm")
@RequestMapping("/phicomm/front")
public class PhicommFrontController  extends  BaseController{

	@RequestMapping("")
	String home(Model model) {
		Map<String, Object> configInfo = new HashMap<String, Object>();
		configInfo.put("product", PhicommProductConfig.PHICOMM_PRODUCT_CONFIG);
		List<Map<String, String>> payTypeList = new ArrayList<Map<String, String>>();
		for (String key : PhicommPayType.PAY_TYPE.keySet()) {
			Map<String, String> payTypeMap = new HashMap<String, String>();
			payTypeMap.put("key", key);
			payTypeMap.put("value", PhicommPayType.PAY_TYPE.get(key));
			payTypeList.add(payTypeMap);
		}
		configInfo.put("payType", payTypeList);
		configInfo.put("notice" , PhicommConstants.BUY_NOTICE);
		model.addAttribute("configInfo", configInfo);
		return "phicomm";
	}

	@RequestMapping("/homeInfo")
	@ResponseBody
	PhicommMessage homeInfo() {
		PhicommBuyHomeInfo phicommBuyHomeInfo = new PhicommBuyHomeInfo();
		phicommBuyHomeInfo.setBuyMaxPerson(PhicommConstants.LIMIT_THREAD_MAX);
		phicommBuyHomeInfo.setSameTimeBuyPerson(PhicommThreadPool.PHICOMMHTREADPOOL.size());
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS,
				null, phicommBuyHomeInfo);
	}

	@RequestMapping("/getBuyInfo")
	@ResponseBody
	PhicommMessage getBuyInfo(@RequestParam String phone) {
		PhicommMessage PhicommMessage = new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, null);
		if (StringUtils.isNoneBlank(phone)) {
			PhicommMessage.setMessage(PhicommLogInfo.logInfo("get", phone, null , null));
		}
		return PhicommMessage;
	}

	@RequestMapping("/stop")
	@ResponseBody
	PhicommMessage stop(@RequestParam String phone) {
		PhicommThreadPool.PHICOMMHTREADPOOL.remove(phone);
		PhicommLogInfo.logInfo("get", phone, null , null);
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, String.format("%s抢购已经停止。", phone));
	}

	@RequestMapping("/coupon")
	@ResponseBody
	PhicommMessage coupon(@RequestParam String phone, @RequestParam String password) {
		PhicommBuyInfo phicommBuyInfo = new PhicommBuyInfo();
		if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(password)) {
			phicommBuyInfo.setPhone(phone);
			phicommBuyInfo.setPassword(password);
			PhicommCouponCore phicommCouponCore = new PhicommCouponCore(phicommBuyInfo);
			phicommCouponCore.run();
			/*
			 * List<Map<String,String>> list = new
			 * ArrayList<Map<String,String>>(); Map<String,String> map = new
			 * HashMap<String,String>(); map.put("code" , "1234");
			 * map.put("name" , "优惠券1"); map.put("lastUseDate" , "2016-01-01");
			 * list.add(map); Map<String,String> map2 = new
			 * HashMap<String,String>(); map2.put("code" , "567");
			 * map2.put("name" , "优惠券1"); map2.put("lastUseDate" ,
			 * "2016-01-01"); list.add(map2);
			 * phicommBuyConfig.setCouponList(list);
			 */
		}
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, "成功", phicommBuyInfo.getCouponList());
	}

	@RequestMapping("/buy")
	@ResponseBody
	PhicommMessage buy(@ModelAttribute PhicommBuyInfo phicommBuyInfo) {
		covert(phicommBuyInfo);
		PhicommMessage phicommMessage = check(phicommBuyInfo);
		if (null != phicommMessage) {
			return phicommMessage;
		}
		PhicommFastBuyCore thread = new PhicommFastBuyCore(phicommBuyInfo);
		PhicommThreadPool.PHICOMMHTREADPOOL.put(phicommBuyInfo.getPhone(), thread);
		thread.start();
		PhicommLogInfo.logInfo("get", phicommBuyInfo.getPhone(), null , null);
		return new PhicommMessage(PhicommConstants.LOG_STATUS_SUCCESS,
				"抢购信息已添加到云服务器\n\n[快速抢购中,不要关闭页面]\n\n抢购信息请查看页面输出面板");
	}

	@RequestMapping("/getSku")
	String getSku(@RequestParam(required = false) String productName ,Model model) {
		List<PhicommProduct> phicommProductList = PhicommProductConfig.PHICOMM_PRODUCT_CONFIG;
		if(StringUtils.isNotBlank(productName) && null != PhicommProductConfig.PHICOMM_PRODUCT_CONFIG && PhicommProductConfig.PHICOMM_PRODUCT_CONFIG.size() > 0){
			phicommProductList = new ArrayList<PhicommProduct>();
			for(PhicommProduct phicommProduct : PhicommProductConfig.PHICOMM_PRODUCT_CONFIG){
				if(productName.trim().equals(phicommProduct.getName())){
					phicommProductList.add(phicommProduct);
				}
			}
		}
		model.addAttribute("skuList", phicommProductList);
		return "getSku";
	}

	private PhicommMessage covert(PhicommBuyInfo phicommBuyInfo) {
		if (null != phicommBuyInfo) {
			phicommBuyInfo.setDlyTypeId("1");
			if (null != PhicommConstants.REBATE_MODEL && PhicommConstants.REBATE_MODEL) {
				//开启返利模式有些产品还是不计入返利
				if (null != phicommBuyInfo.getBuyName() && null != PhicommRebate.PRODUCT_NOT_REBATE && PhicommRebate.PRODUCT_NOT_REBATE.contains(phicommBuyInfo.getBuyName())) {
					phicommBuyInfo.setRebateVsid(null);
				}else{
					phicommBuyInfo.setRebateVsid(PhicommRebate.PHICOMM_ACCOUNT_REBATE_MAP.get(PhicommConstants.REBATE_TELEPHONE));
				}
			} else {
				phicommBuyInfo.setRebateVsid(null);
			}
			if (null != phicommBuyInfo.getBuyId()) {
				phicommBuyInfo.setFastBuyPage(String.format(PhicommProductConfig.getById(phicommBuyInfo.getBuyId()).getBuyPage(),
						phicommBuyInfo.getNumber()));
			}
			if (phicommBuyInfo.isNeedInvoice() && "0".equals(phicommBuyInfo.getInvoiceType())) {
				phicommBuyInfo.setInvoiceTitle(null);
			}
		}
		return null;
	}

	private PhicommMessage check(PhicommBuyInfo phicommBuyInfo) {
		if (null == phicommBuyInfo) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "请求对象不能为空");
		}else if (null == PhicommProductConfig.getById(phicommBuyInfo.getBuyId())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR,
					"抢购产品ID不存在");
		}else if(null != PhicommProductConfig.getById(phicommBuyInfo.getBuyId()).getLimitBuySwitch() && PhicommProductConfig.getById(phicommBuyInfo.getBuyId()).getLimitBuySwitch()){
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "该产品暂时不能购买");
		}else if(null != PhicommProductConfig.getById(phicommBuyInfo.getBuyId()).getCouponSwitch() && !PhicommProductConfig.getById(phicommBuyInfo.getBuyId()).getCouponSwitch() && StringUtils.isNotBlank(phicommBuyInfo.getCoupon())){
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "该产品不能使用优惠券");
		} else if (StringUtils.isBlank(phicommBuyInfo.getFastBuyPage())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "购买物品页面地址不能为空");
		} else if (StringUtils.isBlank(phicommBuyInfo.getPhone())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "账号不能为空");
		} else if (StringUtils.isBlank(phicommBuyInfo.getPassword())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "密码不能为空");
		} else if (StringUtils.isBlank(phicommBuyInfo.getPayAppId())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "支付方式不能为空");
		} else if (phicommBuyInfo.isNeedInvoice() && StringUtils.isBlank(phicommBuyInfo.getInvoiceType())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "发票类型不能为空");
		} else if (phicommBuyInfo.isNeedInvoice() && "1".equals(phicommBuyInfo.getInvoiceType())
				&& StringUtils.isBlank(phicommBuyInfo.getInvoiceTitle())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "发票抬头和发票号不能为空");
		} else if (phicommBuyInfo.getNumber() < 1 || phicommBuyInfo.getNumber() > 5) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "购买数量不能小于0不能超过5");
		} else if (StringUtils.length(phicommBuyInfo.getPhone()) != 11) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "手机号不为11位");
		} else if (PhicommThreadPool.PHICOMMHTREADPOOL.size() >= PhicommConstants.LIMIT_THREAD_MAX) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR,String.format("服务器已经排满最多%s账号同时抢购" , PhicommConstants.LIMIT_THREAD_MAX));
		} else if (null != PhicommThreadPool.PHICOMMHTREADPOOL.get(phicommBuyInfo.getPhone())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR,
					String.format("服务器已存在手机号%s抢购信息,如需要重新添加,请用前台的停止按钮删除抢购信息在添加" , phicommBuyInfo.getPhone()));
		}
		return null;
	}

	public static void main(String[] args) {
		// 黑名单存储路径
		PhicommBlackList.load();
		// 用户存储路径
		PhicommAccountList.load();
		// 配置存储路径
		PhicommConfigList.load();
		PhicommHeartBeat.startCheck();
		PhicommSkuCore.queryAndNofitySku();
		PhicommAutoSaveConfig.startAutoSaveConfig();
		SpringApplication.run(PhicommFrontController.class, args);
	}

}
