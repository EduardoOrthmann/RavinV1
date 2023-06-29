import command.CommandView;
import customer.CustomerView;
import employee.EmployeeView;
import menu.MenuView;
import order.OrderView;
import product.ProductView;
import table.TableView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        do {
            String[] entityOptions = {"Cliente", "Funcionário", "Produto", "Menu", "Mesa", "Pedido", "Comanda"};

            String selectedEntityOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Insira uma classe",
                    "Menu do Restaurante",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    entityOptions,
                    null
            );

            switch (selectedEntityOption) {
                case "Cliente" -> CustomerView.view();
                case "Funcionário" -> EmployeeView.view();
                case "Produto" -> ProductView.view();
                case "Menu" -> MenuView.view();
                case "Mesa" -> TableView.view();
                case "Pedido" -> OrderView.view();
                case "Comanda" -> CommandView.view();
            }

        } while (JOptionPane.showConfirmDialog(null, "Deseja continuar?") == JOptionPane.YES_OPTION);
    }
}