package demo.jaxrs.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleLoggingAspect {
    private Log log = LogFactory.getLog(SimpleLoggingAspect.class);
    public void logBefore() {
        log.info("===============================AOP before advice==============================");
    }

    public void logAfter() {
        log.info("===============================AOP after advice===============================");
    }
}
