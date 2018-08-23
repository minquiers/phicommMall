package logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherUtils {

    private static final Logger logger = LoggerFactory.getLogger(OtherUtils.class);

    public static void info(String msg){
        logger.info(msg);
    }

    public static void errorException(String msg, Throwable t){
        logger.error(msg, t);
    }
}
