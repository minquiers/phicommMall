package phicomm.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowUtils {

    private static final Logger logger = LoggerFactory.getLogger(FlowUtils.class);

    public static void info(String msg){
        logger.info(msg);
    }

    public static void errorException(String msg, Throwable t){
        logger.error(msg, t);
    }
}
