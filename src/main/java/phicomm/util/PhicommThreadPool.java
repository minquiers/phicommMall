package phicomm.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import phicomm.core.PhicommFastBuyCore;
import phicomm.core.PhicommGameCore;
import phicomm.core.PhicommGameDDCow;

public class PhicommThreadPool {

	//每个抢购任务线程池
	public static Map<String, PhicommFastBuyCore> PHICOMMHTREADPOOL = new ConcurrentHashMap<String, PhicommFastBuyCore>();
	//游戏任务线程池
	public static Map<String, PhicommGameCore> PHICOMM_GAME_HTREAD_POOL = new ConcurrentHashMap<String, PhicommGameCore>();
	//游戏天天牛任务线程池
	public static Map<String, PhicommGameDDCow> PHICOMM_GAMETTCOW_HTREAD_POOL = new ConcurrentHashMap<String, PhicommGameDDCow>();
}
