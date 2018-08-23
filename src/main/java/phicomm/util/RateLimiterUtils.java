package phicomm.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import phicomm.config.PhicommConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限流Util-每分钟执行指定数量请求
 */
public class RateLimiterUtils {
	private static Map<String, LoadingCache<Long,AtomicLong>> resourceLimitMap = new ConcurrentHashMap<String, LoadingCache<Long,AtomicLong>>();

	public static boolean getRateLimit(String resource , Integer max) throws ExecutionException {
		if(null == max){
			return false;
		}
		LoadingCache<Long,AtomicLong> counter = resourceLimitMap.get(resource);
		if (counter == null) {
			synchronized (RateLimiterUtils.class) {
				counter = resourceLimitMap.get(resource);
				if(null == counter){
					counter = CacheBuilder.newBuilder()
			                .expireAfterWrite(1, TimeUnit.MINUTES)
			                .build(new CacheLoader<Long, AtomicLong>() {
			                    @Override
			                    public AtomicLong load(Long seconds) throws Exception {
			                        return new AtomicLong(0);
			                    }
			                });
					resourceLimitMap.put(resource, counter);
					return false;
				}
			}
		}

		long currentSeconds = (System.currentTimeMillis() / 1000 / 60);
		if (counter.get(currentSeconds).incrementAndGet() > max) {
			return true;
		}
		return false;
	}
}
