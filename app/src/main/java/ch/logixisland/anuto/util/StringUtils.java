package ch.logixisland.anuto.util;

import java.text.DecimalFormat;

public final class StringUtils {
    private StringUtils() {

    }

    private static DecimalFormat fmt0 = new DecimalFormat("#");
    private static DecimalFormat fmt1 = new DecimalFormat("#.#");

    public static String formatSuffix(float number) {
        if (number >= 1e10f) {
            return fmt0.format(number / 1e9) + "G";
        }
        if (number >= 1e7f) {
            return fmt0.format(number / 1e6) + "M";
        }
        if (number >= 1e4f) {
            return fmt0.format(number / 1e3) + "k";
        }
        if (number < 10f) {
            return fmt1.format(number);
        }

        return fmt0.format(number);
    }
}
