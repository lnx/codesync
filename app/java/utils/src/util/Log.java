package util;

import org.joda.time.DateTime;

public class Log {

    public static void log(String msg) {
        if (msg != null && !msg.matches("[ \t\n]*")) {
            System.out.println("[INF][" + getTimestamp() + "] " + msg.trim());
        }
    }

    public static void error(String msg) {
        if (msg != null && !msg.matches("[ \t\n]*")) {
            System.out.println("[ERR][" + getTimestamp() + "] " + msg.trim());
        }
    }

    private static String getTimestamp() {
        return new DateTime().toString("yyyy-MM-dd hh:mm:ss");
    }

}
