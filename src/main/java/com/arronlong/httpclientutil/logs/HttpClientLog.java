package com.arronlong.httpclientutil.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientLog {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientLog.class);

    public static void info(String msg){
        logger.info(msg);
    }

    public static void errorException(String msg, Throwable t){
        logger.error(msg, t);
    }
}
