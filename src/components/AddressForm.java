package components;

import address.Address;

import javax.swing.*;

public class AddressForm {
    public static Address showInputLocalDateDialog(String message) {
        JOptionPane.showMessageDialog(null, message);

        var country = JOptionPane.showInputDialog("Insira o país:");
        var state = JOptionPane.showInputDialog("Insira o estado:");
        var city = JOptionPane.showInputDialog("Insira a cidade:");
        var zipCode = JOptionPane.showInputDialog("Insira o CEP:");
        var neighborhood = JOptionPane.showInputDialog("Insira o bairro:");
        var street = JOptionPane.showInputDialog("Insira a rua:");

        return new Address(country, state, city, zipCode, neighborhood, street);
    }

    public static Address showInputLocalDateDialog(String message, Address initialSelectionValue) {
        JOptionPane.showMessageDialog(null, message);

        var country = JOptionPane.showInputDialog("Insira o país:", initialSelectionValue.getCountry());
        var state = JOptionPane.showInputDialog("Insira o estado:", initialSelectionValue.getState());
        var city = JOptionPane.showInputDialog("Insira a cidade:", initialSelectionValue.getCity());
        var zipCode = JOptionPane.showInputDialog("Insira o CEP:", initialSelectionValue.getZipCode());
        var neighborhood = JOptionPane.showInputDialog("Insira o bairro:", initialSelectionValue.getNeighborhood());
        var street = JOptionPane.showInputDialog("Insira a rua:", initialSelectionValue.getStreet());

        return new Address(country, state, city, zipCode, neighborhood, street);
    }
}
