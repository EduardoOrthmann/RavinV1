package product;

import employee.Employee;
import employee.EmployeeService;
import templates.BooleanField;
import templates.LocalTimeField;

import javax.swing.*;
import java.time.LocalTime;

public class ProductView {
    private final ProductService productService;
    public final EmployeeService employeeService;

    public ProductView(ProductService productService, EmployeeService employeeService) {
        this.productService = productService;
        this.employeeService = employeeService;
    }

    public void view() {
        String[] productOptions = {"findById", "findAll", "save", "update", "delete"};

        String selectedProductOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Produto",
                JOptionPane.QUESTION_MESSAGE,
                null,
                productOptions,
                null
        );

        switch (selectedProductOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(productService.findById(id));
            }

            case "findAll" -> System.out.println(productService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Insira um funcionário antes de inserir um produto");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira o nome:");
                var description = JOptionPane.showInputDialog("Insira a descrição:");
                var productCode = JOptionPane.showInputDialog("Insira o código do produto:");
                var costPrice = Double.parseDouble(JOptionPane.showInputDialog("Insira o preço de custo:"));
                var salePrice = Double.parseDouble(JOptionPane.showInputDialog("Insira o preço de venda:"));
                var hourOfPreparation = Integer.parseInt(JOptionPane.showInputDialog("Insira a quantidade de horas de preparo:"));
                var minuteOfPreparation = Integer.parseInt(JOptionPane.showInputDialog("Insira a quantidade de minutos de preparo:"));
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está criando esse produto?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var preparationTime = LocalTime.of(hourOfPreparation, minuteOfPreparation);
                var createdByEmployee = employeeService.findById((int) createdBy).getRole();

                productService.save(
                        new Product(
                                name,
                                description,
                                productCode,
                                costPrice,
                                salePrice,
                                preparationTime,
                                createdByEmployee
                        )
                );
            }

            case "update" -> {
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (products.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um produto antes de atualizar um produto");
                    break;
                }

                var id = (int) JOptionPane.showInputDialog(
                        null,
                        "Qual produto você deseja alterar?",
                        "Alterar",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        null
                );

                var product = productService.findById(id);

                var name = JOptionPane.showInputDialog("Insira um nome:", product.getName());
                var description = JOptionPane.showInputDialog("Insira uma descrição:", product.getDescription());
                var productCode = JOptionPane.showInputDialog("Insira o código do produto:", product.getProductCode());
                var costPrice = Double.parseDouble(JOptionPane.showInputDialog("Insira o preço de custo:", product.getCostPrice()));
                var salePrice = Double.parseDouble(JOptionPane.showInputDialog("Insira o preço de venda:", product.getSalePrice()));
                var isAvailable = BooleanField.showInputBooleanDialog("O produto está disponível?", product.isAvailable());
                var preparationTime = LocalTimeField.showInputLocalTimeDialog("Insira o tempo de preparação:", product.getPreparationTime());
                var selectedUpdatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está alterando esse produto?",
                        "Alterado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var updatedBy = employeeService.findById((int) selectedUpdatedBy).getRole();

                product.setId(id);
                product.setName(name);
                product.setDescription(description);
                product.setProductCode(productCode);
                product.setCostPrice(costPrice);
                product.setSalePrice(salePrice);
                product.setPreparationTime(preparationTime);
                product.setAvailable(isAvailable);
                product.setUpdatedBy(updatedBy);

                productService.update(product);
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                productService.delete(productService.findById(id));
            }
        }
    }
}
