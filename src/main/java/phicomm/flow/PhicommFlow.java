package phicomm.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhicommFlow extends FlowBase {
	//购买流程
	public static List<Map<String,Object>> PHICOMM_BUY_FLOW = new ArrayList<Map<String,Object>>();
	//购买校验流程
	public static List<Map<String,Object>> PHICOMM_BUY_CHECK_FLOW = new ArrayList<Map<String,Object>>();
	//优惠券流程
	public static List<Map<String,Object>> PHICOMM_COUPON_FLOW = new ArrayList<Map<String,Object>>();
	//游戏流程-橙子游戏
	public static List<Map<String,Object>> PHICOMM_GAME_FLOW = new ArrayList<Map<String,Object>>();
	//游戏流程-天天牛
	public static List<Map<String,Object>> PHICOMM_GAMEDDCOW_FLOW = new ArrayList<Map<String,Object>>();
	//查询库存流程
	public static List<Map<String,Object>> PHICOMM_QUERY_FLOW = new ArrayList<Map<String,Object>>();
	//查询结算流程
	public static List<Map<String,Object>> PHICOMM_QUERY_CLEARING_FLOW = new ArrayList<Map<String,Object>>();
	/*//备份(用于检测是否修改过流程)
	public static List<Map<String,Object>> PHICOMM_FLOW_BACKUP = new ArrayList<Map<String,Object>>();
	
	public static void main(String[] args) {
		
	}
	
	*//**
	 * 同步流程
	 *//*
	public static synchronized void sychFlow(){
		PHICOMM_FLOW_BACKUP.clear();
		copyProperties(PHICOMM_FLOW, PHICOMM_FLOW_BACKUP);
	}
	
	*//**
	 * 检测流程名称是否修改
	 * @param flowName
	 * @return
	 *//*
	public static boolean checkFlowIsModifi(String flowName){
		boolean isModifi = false;
		for(int i = 0 ; i < PHICOMM_FLOW_BACKUP.size();i++){
			Map<String,Object> backup = PHICOMM_FLOW_BACKUP.get(i);
			if(flowName.equals(backup.get("name"))){
				Map<String,Object> current = PHICOMM_FLOW.get(i);
				if(!current.get("name").equals(backup.get("name"))){
					isModifi = true;
					break;
				}else if(!current.get("skip").equals(backup.get("skip"))){
					isModifi = true;
					break;
				}else if(!current.get("errorRepeatExec").equals(backup.get("errorRepeatExec"))){
					isModifi = true;
					break;
				}else if(!current.get("sleepTime").equals(backup.get("sleepTime"))){
					isModifi = true;
					break;
				}
				break;
			}
		}
		return isModifi;
	}*/
	
	
	
	/**
	 * 更新流程
	 * @param flow
	 */
	public static synchronized void update(Map<String,Object> flow){
		for(int i = 0 ; i < PHICOMM_BUY_FLOW.size();i++){
			Map<String,Object> current = PHICOMM_BUY_FLOW.get(i);
			if(flow.get("name").equals(current.get("name"))){
				PHICOMM_BUY_FLOW.add(i, flow);
				PHICOMM_BUY_FLOW.remove(i+1);
				break;
			}
		}
	}
	
	/**
	 * 拷贝属性
	 * @param source
	 * @param target
	 *//*
	private static synchronized void copyProperties(List<Map<String,Object>> source , List<Map<String,Object>> target){
		target.clear();
		Map<String,Object> temp = null;
		for(Map<String,Object> map : source){
			temp = new HashMap<String,Object>();
			try {
				BeanUtils.populate(temp, map);
			} catch (Exception e) {
				Utils.errorException(e.getMessage() ,e);
			}
			target.add(temp);
		}
	}*/
	
	static{
		/**
		 * 1.访问登录页面|是否跳过流程|失败一直执行|每次执行完休眠时间
		 * 2.访问登录页面|是否跳过流程|失败一直执行|每次执行完休眠时间
		 * 3.立即购买|是否跳过流程|失败一直执行|每次执行完休眠时间
		 * 4.订单详情|是否跳过流程|失败一直执行|每次执行完休眠时间
		 * 5.使用优惠券|是否跳过流程|失败一直执行|每次执行完休眠时间
		 * 6.确认购买|是否跳过流程|失败一直执行|每次执行完休眠时间
		 */
		/*----------------------购买流程----------------------*/
		Map<String,Object> visitLoginPageMap = new HashMap<String,Object>();
		visitLoginPageMap.put("name", "visitLoginPage"); //流程名称
		visitLoginPageMap.put("skip", false);  //是否跳过流程
		visitLoginPageMap.put("errorRepeatExec", true); //错误是否一直执行
		visitLoginPageMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> loginMap = new HashMap<String,Object>();
		loginMap.put("name", "login"); //流程名称
		loginMap.put("skip", false);  //是否跳过流程
		loginMap.put("errorRepeatExec", true); //错误是否一直执行
		loginMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> orderCenterMap = new HashMap<String,Object>();
		orderCenterMap.put("name", "orderCenter"); //流程名称
		orderCenterMap.put("skip", false);  //是否跳过流程
		orderCenterMap.put("errorRepeatExec", false); //错误是否一直执行
		orderCenterMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> checkExistsOrderMap = new HashMap<String,Object>();
		checkExistsOrderMap.put("name", "checkExistsOrder"); //流程名称
		checkExistsOrderMap.put("skip", false);  //是否跳过流程
		checkExistsOrderMap.put("errorRepeatExec", "break"); //错误是否一直执行
		checkExistsOrderMap.put("sleepTime", 1); //每次执行休眠时间(毫秒)
		
		Map<String,Object> addShoppingCartMap = new HashMap<String,Object>();
		addShoppingCartMap.put("name", "addShoppingCart"); //流程名称
		addShoppingCartMap.put("skip", false);  //是否跳过流程
		addShoppingCartMap.put("errorRepeatExec", true); //错误是否一直执行
		addShoppingCartMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> orderDetailMap = new HashMap<String,Object>();
		orderDetailMap.put("name", "orderDetail"); //流程名称
		orderDetailMap.put("skip", false);  //是否跳过流程
		orderDetailMap.put("errorRepeatExec", true); //错误是否一直执行
		orderDetailMap.put("sleepTime", 1500); //每次执行休眠时间(毫秒)
		
		Map<String,Object> useCouponMap = new HashMap<String,Object>();
		useCouponMap.put("name", "useCoupon"); //流程名称
		useCouponMap.put("skip", false);  //是否跳过流程
		useCouponMap.put("errorRepeatExec", true); //错误是否一直执行
		useCouponMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)

		Map<String,Object> buyVcodeMap = new HashMap<String,Object>();
		buyVcodeMap.put("name", "buyVcode"); //流程名称
		buyVcodeMap.put("skip", false);  //是否跳过流程
		buyVcodeMap.put("errorRepeatExec", true); //错误是否一直执行
		buyVcodeMap.put("sleepTime", 600); //每次执行休眠时间(毫秒)


		Map<String,Object> buyMap = new HashMap<String,Object>();
		buyMap.put("name", "buy"); //流程名称
		buyMap.put("skip", false);  //是否跳过流程
		buyMap.put("errorRepeatExec", true); //错误是否一直执行
		buyMap.put("sleepTime", 3000); //每次执行休眠时间(毫秒)
		
		
		PHICOMM_BUY_FLOW.add(visitLoginPageMap);
		PHICOMM_BUY_FLOW.add(loginMap);
		PHICOMM_BUY_FLOW.add(orderCenterMap);
		PHICOMM_BUY_FLOW.add(checkExistsOrderMap);
		PHICOMM_BUY_FLOW.add(addShoppingCartMap);
		PHICOMM_BUY_FLOW.add(orderDetailMap);
		PHICOMM_BUY_FLOW.add(useCouponMap);
		PHICOMM_BUY_FLOW.add(buyVcodeMap);
		PHICOMM_BUY_FLOW.add(buyMap);
		
		/*----------------------购买校验流程----------------------*/
		orderCenterMap = new HashMap<String,Object>();
		orderCenterMap.put("name", "orderCenter"); //流程名称
		orderCenterMap.put("skip", false);  //是否跳过流程
		orderCenterMap.put("errorRepeatExec", true); //错误是否一直执行
		orderCenterMap.put("sleepTime", 8500); //每次执行休眠时间(毫秒)
		PHICOMM_BUY_CHECK_FLOW.add(orderCenterMap);
		
		
		/*----------------------优惠券流程----------------------*/
		visitLoginPageMap = new HashMap<String,Object>();
		visitLoginPageMap.put("name", "visitLoginPage"); //流程名称
		visitLoginPageMap.put("skip", false);  //是否跳过流程
		visitLoginPageMap.put("errorRepeatExec", false); //错误是否一直执行
		visitLoginPageMap.put("sleepTime", 2000l); //每次执行休眠时间(毫秒)
		
		loginMap = new HashMap<String,Object>();
		loginMap.put("name", "login"); //流程名称
		loginMap.put("skip", false);  //是否跳过流程
		loginMap.put("errorRepeatExec", false); //错误是否一直执行
		loginMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> visitCouponPageMap = new HashMap<String,Object>();
		visitCouponPageMap.put("name", "visitCouponPage"); //流程名称
		visitCouponPageMap.put("skip", false);  //是否跳过流程
		visitCouponPageMap.put("errorRepeatExec", false); //错误是否一直执行
		visitCouponPageMap.put("sleepTime", 1500); //每次执行休眠时间(毫秒)
		
		PHICOMM_COUPON_FLOW.add(visitLoginPageMap);
		PHICOMM_COUPON_FLOW.add(loginMap);
		PHICOMM_COUPON_FLOW.add(visitCouponPageMap);
		
		/*----------------------游戏流程----------------------*/
		visitLoginPageMap = new HashMap<String,Object>();
		visitLoginPageMap.put("name", "visitLoginPage"); //流程名称
		visitLoginPageMap.put("skip", false);  //是否跳过流程
		visitLoginPageMap.put("errorRepeatExec", false); //错误是否一直执行
		visitLoginPageMap.put("sleepTime", 2000l); //每次执行休眠时间(毫秒)
		
		loginMap = new HashMap<String,Object>();
		loginMap.put("name", "login"); //流程名称
		loginMap.put("skip", false);  //是否跳过流程
		loginMap.put("errorRepeatExec", false); //错误是否一直执行
		loginMap.put("sleepTime", 2000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> visitGameHomeMap = new HashMap<String,Object>();
		visitGameHomeMap.put("name", "visitGameHome"); //流程名称
		visitGameHomeMap.put("skip", false);  //是否跳过流程
		visitGameHomeMap.put("errorRepeatExec", false); //错误是否一直执行
		visitGameHomeMap.put("sleepTime", 1500); //每次执行休眠时间(毫秒)
		
		Map<String,Object> playGameMap = new HashMap<String,Object>();
		playGameMap.put("name", "playGame"); //流程名称
		playGameMap.put("skip", false);  //是否跳过流程
		playGameMap.put("errorRepeatExec", false); //错误是否一直执行
		playGameMap.put("sleepTime", 1500); //每次执行休眠时间(毫秒)

		Map<String,Object> getGameScoreMap = new HashMap<String,Object>();
		getGameScoreMap.put("name", "getGameScore"); //流程名称
		getGameScoreMap.put("skip", false);  //是否跳过流程
		getGameScoreMap.put("errorRepeatExec", false); //错误是否一直执行
		getGameScoreMap.put("sleepTime", 1500); //每次执行休眠时间(毫秒)
		PHICOMM_GAME_FLOW.add(visitLoginPageMap);//每次执行休眠时间(毫秒)
		PHICOMM_GAME_FLOW.add(loginMap);
		PHICOMM_GAME_FLOW.add(visitGameHomeMap);
		PHICOMM_GAME_FLOW.add(playGameMap);
		PHICOMM_GAME_FLOW.add(getGameScoreMap);

		/*----------------------查询库存流程----------------------*/
		Map<String,Object> querySkuMap = new HashMap<String,Object>();
		querySkuMap.put("name", "querySku"); //流程名称
		querySkuMap.put("skip", false);  //是否跳过流程
		querySkuMap.put("errorRepeatExec", false); //错误是否一直执行
		querySkuMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		PHICOMM_QUERY_FLOW.add(querySkuMap);
		
		/*----------------------游戏天天牛流程----------------------*/
		visitLoginPageMap = new HashMap<String,Object>();
		visitLoginPageMap.put("name", "visitLoginPage"); //流程名称
		visitLoginPageMap.put("skip", false);  //是否跳过流程
		visitLoginPageMap.put("errorRepeatExec", false); //错误是否一直执行
		visitLoginPageMap.put("sleepTime", 2000l); //每次执行休眠时间(毫秒)
		
		loginMap = new HashMap<String,Object>();
		loginMap.put("name", "login"); //流程名称
		loginMap.put("skip", false);  //是否跳过流程
		loginMap.put("errorRepeatExec", true); //错误是否一直执行
		loginMap.put("sleepTime", 2000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> visitGameDDCowHomeMap = new HashMap<String,Object>();
		visitGameDDCowHomeMap.put("name", "visitGameDDCowHome"); //流程名称
		visitGameDDCowHomeMap.put("skip", false);  //是否跳过流程
		visitGameDDCowHomeMap.put("errorRepeatExec", true); //错误是否一直执行
		visitGameDDCowHomeMap.put("sleepTime", 1000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> gameDDCowMap = new HashMap<String,Object>();
		gameDDCowMap.put("name", "gameDDCow"); //流程名称
		gameDDCowMap.put("skip", false);  //是否跳过流程
		gameDDCowMap.put("errorRepeatExec", true); //错误是否一直执行
		gameDDCowMap.put("sleepTime", 800); //每次执行休眠时间(毫秒)
		

		PHICOMM_GAMEDDCOW_FLOW.add(visitLoginPageMap);
		PHICOMM_GAMEDDCOW_FLOW.add(loginMap);
		PHICOMM_GAMEDDCOW_FLOW.add(visitGameDDCowHomeMap);
		PHICOMM_GAMEDDCOW_FLOW.add(gameDDCowMap);
		
		/*----------------------查询结算流程----------------------*/
		visitLoginPageMap = new HashMap<String,Object>();
		visitLoginPageMap.put("name", "visitLoginPage"); //流程名称
		visitLoginPageMap.put("skip", false);  //是否跳过流程
		visitLoginPageMap.put("errorRepeatExec", false); //错误是否一直执行
		visitLoginPageMap.put("sleepTime", 2000l); //每次执行休眠时间(毫秒)
		
		loginMap = new HashMap<String,Object>();
		loginMap.put("name", "login"); //流程名称
		loginMap.put("skip", false);  //是否跳过流程
		loginMap.put("errorRepeatExec", true); //错误是否一直执行
		loginMap.put("sleepTime", 2000); //每次执行休眠时间(毫秒)
		
		Map<String,Object> queryClearing = new HashMap<String,Object>();
		queryClearing.put("name", "queryClearing"); //流程名称
		queryClearing.put("skip", false);  //是否跳过流程
		queryClearing.put("errorRepeatExec", true); //错误是否一直执行
		queryClearing.put("sleepTime", 2000); //每次执行休眠时间(毫秒)
		
		
		PHICOMM_QUERY_CLEARING_FLOW.add(visitLoginPageMap);
		PHICOMM_QUERY_CLEARING_FLOW.add(loginMap);
		PHICOMM_QUERY_CLEARING_FLOW.add(queryClearing);
	}
}
