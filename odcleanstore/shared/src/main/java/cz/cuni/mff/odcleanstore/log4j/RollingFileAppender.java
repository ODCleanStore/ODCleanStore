package cz.cuni.mff.odcleanstore.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Log4j Rolling file appender with switching output to different files.
 * Work independently in each thread.
 * 
 * @author Petr Jerman
 */
public class RollingFileAppender extends org.apache.log4j.RollingFileAppender {

    private static final String CURRENT_APPENDER = "odcs_log4j_appender_context";

    private RollingFileAppender previousRollingFileAppender = null;

    /**
     * The default constructor simply calls its parents constructor
     * and mark as default appender for thread and child thread
     * if default appender does not exist.
     * 
     */
    public RollingFileAppender() {
        super();
        if (getCurrent() == null) {
            MDC.put(CURRENT_APPENDER, this);
        }
    }

    /**
     * Create odcs log4j RollingFileAppender for using in setNewLogFile method.
     * 
     * @param previous RollingFileAppender for setting
     *        new appender in popPreviousLogFile method
     *        and from which Layout, MaximumFileSize and MaxBackupIndex are coppied.
     * @param logFileName new logging file name
     */
    private RollingFileAppender(RollingFileAppender previous, String logFileName) {
        super();
        try {
            this.setAppend(true);
            this.setFile(logFileName);
            this.previousRollingFileAppender = previous;
            if (previous != null) {
                this.setLayout(previous.getLayout());
                this.setMaximumFileSize(previous.getMaximumFileSize());
                this.setMaxBackupIndex(previous.getMaxBackupIndex());
            } else {
                this.setLayout(new PatternLayout("%p %t %c - %m%n"));
            }
            this.activateOptions();
        } catch (Exception e) {
            // do nothing
        }
    }

    /**
     * Set odcs log4j new RollingFileAppender for thread and its next child,
     * previous RollingFileAppender is hidden.
     * 
     * @param logFileName name of new logging file name
     */
    public static synchronized void setNewLogFile(String logFileName) {
        RollingFileAppender previous = getCurrent();
        RollingFileAppender appender = new RollingFileAppender(previous, logFileName);
        try {
            Logger.getRootLogger().addAppender(appender);
        } catch (Exception e) {
            // do nothing
        }
        MDC.put(CURRENT_APPENDER, appender);
    }

    /**
     * Set odcs log4j RollingFileAppender for thread and its next child,
     * which was hidden by previous calling of setNewLogFile method.
     */
    public static synchronized void popPreviousLogFile() {
        RollingFileAppender current = getCurrent();
        if (current != null) {
            if (current.previousRollingFileAppender != null) {
                MDC.put(CURRENT_APPENDER, current.previousRollingFileAppender);
            } else {
                MDC.remove(CURRENT_APPENDER);
            }
            Logger.getRootLogger().removeAppender(current);
        } else {
            MDC.remove(CURRENT_APPENDER);
        }
    }

    /**
     * @return get current odcs log4j thread RollingFileAppender.
     */
    private static RollingFileAppender getCurrent() {
        Object current = MDC.get(CURRENT_APPENDER);
        return current instanceof RollingFileAppender ? (RollingFileAppender) current : null;
    }

    /**
     * If object is current odcs log4j thread RollingFileAppender, then append message to the log.
     * 
     * @see org.apache.log4j.WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    public void append(LoggingEvent event) {
        if (event.getMDC(CURRENT_APPENDER) == this) {
            super.append(event);
        }
    }
}
