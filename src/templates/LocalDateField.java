package templates;

import javax.swing.*;
import java.time.LocalDate;

public class LocalDateField {
    public static LocalDate showInputLocalDateDialog(String message, LocalDate initialSelectionValue) {
        JOptionPane.showMessageDialog(null, message);

        var day = Integer.parseInt(JOptionPane.showInputDialog("dia:", initialSelectionValue.getDayOfMonth()));
        var month = Integer.parseInt(JOptionPane.showInputDialog("mês:", initialSelectionValue.getMonth().getValue()));
        var year = Integer.parseInt(JOptionPane.showInputDialog("ano:", initialSelectionValue.getYear()));

        return LocalDate.of(year, month, day);
    }

    public static LocalDate showInputLocalDateDialog(String message) {
        JOptionPane.showMessageDialog(null, message);

        var day = Integer.parseInt(JOptionPane.showInputDialog("dia:"));
        var month = Integer.parseInt(JOptionPane.showInputDialog("mês:"));
        var year = Integer.parseInt(JOptionPane.showInputDialog("ano:"));

        return LocalDate.of(year, month, day);
    }
}
