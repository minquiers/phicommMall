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
import phicomm.config.PhicommCowVersion;
import phicomm.core.PhicommGameDDCow;
import phicomm.core.PhicommHeartBeat;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.PhicommAccountList;
import phicomm.util.PhicommBlackList;
import phicomm.util.PhicommConfigList;
import phicomm.util.PhicommLogInfo;
import phicomm.util.PhicommMessage;
import phicomm.util.PhicommThreadPool;

@Controller
@SpringBootApplication(scanBasePackages = "phicomm")
@ServletComponentScan(basePackages = "phicomm")
@RequestMapping("/phicomm/front/gameDDCow")
public class PhicommFrontGameDDCowController  extends  BaseController{

	@RequestMapping("")
	String home(Model model) {
		Map<String, Object> configInfo = new HashMap<String, Object>();
		List<Map<String, String>> cowVersionList = new ArrayList<Map<String, String>>();
		for (String key : PhicommCowVersion.COW_VERSION.keySet()) {
			Map<String, String> cowVersionMap = new HashMap<String, String>();
			cowVersionMap.put("key", key);
			cowVersionMap.put("value", PhicommCowVersion.COW_VERSION.get(key));
			cowVersionList.add(cowVersionMap);
		}
		configInfo.put("versionList", cowVersionList);
		configInfo.put("notice", PhicommConstants.DDCOW_NOTICE);
		model.addAttribute("configInfo", configInfo);
		return "phicommGameDDCow";
	}

	@RequestMapping("/getGameResult")
	@ResponseBody
	PhicommMessage getGameResult(@RequestParam String phone) {
		PhicommMessage PhicommMessage = new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, null);
		if (StringUtils.isNoneBlank(phone)) {
			PhicommMessage.setMessage(PhicommLogInfo.logInfo("get", phone, null , PhicommConstants.BUY_TYPE_DDCOW));
		}
		return PhicommMessage;
	}

	@RequestMapping("/playGame")
	@ResponseBody
	PhicommMessage playGame(@ModelAttribute PhicommBuyInfo phicommBuyInfo) {
		PhicommMessage phicommMessage = check(phicommBuyInfo);
		if (null != phicommMessage) {
			return phicommMessage;
		}
		phicommBuyInfo.setDdnVersion(PhicommCowVersion.COW_VERSION.get(phicommBuyInfo.getDdnVersion()));
		PhicommGameDDCow thread = new PhicommGameDDCow(phicommBuyInfo);
		thread.start();
		return new PhicommMessage(PhicommConstants.LOG_STATUS_SUCCESS,
				"已经开始抢牛,请查看输出控制台");
	}
	
	@RequestMapping("/stop")
	@ResponseBody
	PhicommMessage stop(@RequestParam String phone) {
		PhicommThreadPool.PHICOMM_GAMETTCOW_HTREAD_POOL.remove(phone);
		PhicommLogInfo.logInfo("get", phone, null , PhicommConstants.BUY_TYPE_DDCOW);
		return new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, String.format("%s抢牛已经停止。", phone));
	}

	private PhicommMessage check(PhicommBuyInfo phicommBuyInfo) {
		if (null == phicommBuyInfo) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "请求对象不能为空");
		}else if (StringUtils.isBlank(phicommBuyInfo.getPhone())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "账号不能为空");
		} else if (StringUtils.isBlank(phicommBuyInfo.getPassword())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "密码不能为空");
		} else if (StringUtils.length(phicommBuyInfo.getPhone()) != 11) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "手机号不为11位");
		} else if (StringUtils.isBlank(phicommBuyInfo.getDdnVersion())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "未选择抢几代牛");
		} else if (null == PhicommCowVersion.COW_VERSION) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, "未配置牛版本");
		} else if (null == PhicommCowVersion.COW_VERSION.get(phicommBuyInfo.getDdnVersion())) {
			return new PhicommMessage(PhicommConstants.LOG_STATUS_ERROR, String.format("%s代牛版本不存在", phicommBuyInfo.getDdnVersion()));
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// 黑名单存储路径
		PhicommBlackList.load();
		// 用户存储路径
		PhicommAccountList.load();
		// 配置存储路径
		PhicommConfigList.load();
		PhicommHeartBeat.startCheck();
		SpringApplication.run(PhicommFrontGameDDCowController.class, args);
	}

}
