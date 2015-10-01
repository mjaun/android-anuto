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

        if (number >= 1e10f) {
            suffix = "G";
            number /= 1e9f;
        }
        else if (number >= 1e7f) {
            suffix = "M";
            number /= 1e6f;
        }
        else if (number >= 1e4f) {
            suffix = "k";
            number /= 1e3f;
        }

        DecimalFormat fmt = (number < 1e3f && !integer) ? fmt1 : fmt0;
        return fmt.format(number) + suffix;
    }
}
