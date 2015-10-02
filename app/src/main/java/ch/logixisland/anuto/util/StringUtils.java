package ch.logixisland.anuto.util;

import java.text.DecimalFormat;

public final class StringUtils {
    private StringUtils() {

    }

    private static DecimalFormat fmt0 = new DecimalFormat("0");
    private static DecimalFormat fmt1 = new DecimalFormat("0.0");

    public static String formatSuffix(int number) {
        return formatSuffix(number, true);
    }

    public static String formatSuffix(float number) {
        return formatSuffix(number, false);
    }

    public static String formatSuffix(float number, boolean integer) {
        String suffix = "";
        boolean big = false;

        if (number >= 1e10f) {
            suffix = "G";
            number /= 1e9f;
            big = true;
        }
        else if (number >= 1e7f) {
            suffix = "M";
            number /= 1e6f;
            big = true;
        }
        else if (number >= 1e4f) {
            suffix = "k";
            number /= 1e3f;
            big = true;
        }

        DecimalFormat fmt = (number < 1e2f && (!integer || big)) ? fmt1 : fmt0;
        return fmt.format(number) + suffix;
    }
}
