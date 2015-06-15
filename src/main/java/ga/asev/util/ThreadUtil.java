package ga.asev.util;


import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    public static void sleepForDownload() {
        sleep(10, TimeUnit.SECONDS);
    }

    public static void sleep(long timeValue, @NotNull TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeValue);
        } catch (InterruptedException ignored) {}
    }
}
