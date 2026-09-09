package tarot.domain.enums;

import java.time.LocalDate;

public enum ZodiacSign {
    ARIES,
    TAURUS,
    GEMINI,
    CANCER,
    LEO,
    VIRGO,
    LIBRA,
    SCORPIO,
    SAGITTARIUS,
    CAPRICORN,
    AQUARIUS,
    PISCES,
    UNKNOWN;

    public static ZodiacSign fromLocalDate(LocalDate dob) {
        if (dob == null) return UNKNOWN;
        int m = dob.getMonthValue();
        int d = dob.getDayOfMonth();

        return switch (m) {
            case 1 -> (d <= 19) ? CAPRICORN : AQUARIUS;
            case 2 -> (d <= 18) ? AQUARIUS : PISCES;
            case 3 -> (d <= 20) ? PISCES : ARIES;
            case 4 -> (d <= 19) ? ARIES : TAURUS;
            case 5 -> (d <= 20) ? TAURUS : GEMINI;
            case 6 -> (d <= 20) ? GEMINI : CANCER;
            case 7 -> (d <= 22) ? CANCER : LEO;
            case 8 -> (d <= 22) ? LEO : VIRGO;
            case 9 -> (d <= 22) ? VIRGO : LIBRA;
            case 10 -> (d <= 22) ? LIBRA : SCORPIO;
            case 11 -> (d <= 21) ? SCORPIO : SAGITTARIUS;
            case 12 -> (d <= 21) ? SAGITTARIUS : CAPRICORN;
            default -> UNKNOWN;
        };
    }
}