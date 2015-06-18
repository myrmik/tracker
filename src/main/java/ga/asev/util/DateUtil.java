package ga.asev.util;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final PeriodFormatter dateFormat =
            new PeriodFormatterBuilder()
                    .appendDays()
                    .appendSuffix(" day", " days")
                    .appendSeparator(", ")
                    .printZeroIfSupported()
                    .minimumPrintedDigits(2)
                    .appendHours()
                    .appendSeparator(":")
                    .printZeroIfSupported()
                    .minimumPrintedDigits(2)
                    .appendMinutes()
                    .toFormatter();

    public static String formatPeriodTillNowTo(LocalDateTime time) {
        Duration duration = Duration.between(LocalDateTime.now(), time);
        return dateFormat.print(new Period(duration.toMillis()).normalizedStandard());
    }

    public static double getProgressBetween(LocalDateTime before, LocalDateTime after) {
        Duration beforeToNow = Duration.between(before, LocalDateTime.now());
        Duration beforeToAfter = Duration.between(before, after);
        return (double)beforeToNow.toMillis() / beforeToAfter.toMillis();
    }

    public static LocalDateTime zoneToLocal(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return LocalDateTime.from(zonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Kiev")));
    }
}
