package phicomm.controller;

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
import phicomm.core.PhicommGameCore;
import phicomm.core.PhicommHeartBeat;
import phicomm.model.PhicommBuyInfo;
import phicomm.util.*;

@Controller
@SpringBootApplication(scanBasePackages = "phicomm")
@ServletComponentScan(basePackages = "phicomm")
@RequestMapping("/phicomm/front/game")
public class PhicommFrontGameController  extends  BaseController{

	@RequestMapping("")
	String home(Model model) {
		return "phicommGame";
	}

	@RequestMapping("/getGameResult")
	@ResponseBody
	PhicommMessage getGameResult(@RequestParam String phone) {
		PhicommMessage PhicommMessage = new PhicommMessage(PhicommConstants.CONTROLLER_STATUS_SUCCESS, null);
		if (StringUtils.isNoneBlank(phone)) {
			PhicommMessage.setMessage(PhicommLogInfo.logInfo("get", phone, null , PhicommConstants.BUY_TYPE_GAME));
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
		PhicommGameCore thread = new PhicommGameCore(phicommBuyInfo);
		thread.start();
		return new PhicommMessage(PhicommConstants.LOG_STATUS_SUCCESS,
				"已经开始刷分,请查看输出控制台");
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
		SpringApplication.run(PhicommFrontGameController.class, args);
	}

}
