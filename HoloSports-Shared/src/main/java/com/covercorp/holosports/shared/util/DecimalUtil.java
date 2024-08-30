package com.covercorp.holosports.shared.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class DecimalUtil {
    public static String formatDecimal(double decimal) {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        final DecimalFormat decimalFormat = new DecimalFormat("##.##", symbols);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        return decimalFormat.format(decimal);
    }
}
