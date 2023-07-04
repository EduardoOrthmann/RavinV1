package templates;

import javax.swing.*;

public class BooleanField {
    public static final Object[] options = {"True", "False"};

    public static boolean showInputBooleanDialog(String message, Object initialSelectionValue) {
        int selectedOption = JOptionPane.showOptionDialog(
                null,
                message,
                "Campo boolean",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                initialSelectionValue
        );

        return selectedOption == JOptionPane.YES_OPTION;
    }

    public static boolean showInputBooleanDialog(String message) {
        int selectedOption = JOptionPane.showOptionDialog(
                null,
                message,
                "Campo boolean",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null
        );

        return selectedOption == JOptionPane.YES_OPTION;
    }
}
