package utils;

import java.time.LocalDate;
import java.time.Period;

public class DateUtils {
    public static int getAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static boolean isBirthday(LocalDate birthDate) {
        return LocalDate.now().getDayOfMonth() == birthDate.getDayOfMonth() &&
                LocalDate.now().getMonth() == birthDate.getMonth();
    }
}
