package templates;

import javax.swing.*;
import java.time.LocalTime;

public class LocalTimeField {
    public static LocalTime showInputLocalTimeDialog(String message, LocalTime initialSelectionValue) {
        JOptionPane.showMessageDialog(null, message);

        var hour = Integer.parseInt(JOptionPane.showInputDialog("hora:", initialSelectionValue.getHour()));
        var minute = Integer.parseInt(JOptionPane.showInputDialog("minuto:", initialSelectionValue.getMinute()));

        return LocalTime.of(hour, minute);
    }

    public static LocalTime showInputLocalTimeDialog(String message) {
        JOptionPane.showMessageDialog(null, message);

        var hour = Integer.parseInt(JOptionPane.showInputDialog("hora:"));
        var minute = Integer.parseInt(JOptionPane.showInputDialog("minuto:"));

        return LocalTime.of(hour, minute);
    }
}
