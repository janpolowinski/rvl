package org.purl.rvl.tooling.commons.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * @author Jan Polowinski
 *
 */
public class CustomRecordFormatter extends SimpleFormatter {
	
    private final Date dat = new Date();
    
    // setting the formatting string can also be done in the properties file for simple loggers
//    private static final String format = "%4$s: %5$s [%1$tc]%n";
    private static final String format = "%4$-7s %5$s (%2$s) %6$s%n";
    
    @Override
    public String format(LogRecord record) {
//        return record.get + record.getLevel() + ":" + record.getMessage();
    	
    	dat.setTime(record.getMillis());
    	String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
               source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format,
                dat,
                source,
                record.getLoggerName(),
                record.getLevel(),
                record.getMessage(),
                throwable);
    }
}