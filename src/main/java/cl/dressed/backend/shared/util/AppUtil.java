package cl.dressed.backend.shared.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class AppUtil {

    private AppUtil() {
    }

    public static Instant plusMinutesFromNow(long minutes) {
        return Instant.now().plus(minutes, ChronoUnit.MINUTES);
    }
}
