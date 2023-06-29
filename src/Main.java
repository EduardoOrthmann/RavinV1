import address.Address;
import command.Command;
import command.CommandService;
import customer.Customer;
import customer.CustomerService;
import employee.Employee;
import employee.EmployeeService;
import enums.*;
import menu.Menu;
import menu.MenuService;
import order.Order;
import order.OrderService;
import product.Product;
import product.ProductService;
import table.Table;
import table.TableService;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    private static final CustomerService customerService = new CustomerService();
    private static final EmployeeService employeeService = new EmployeeService();
    private static final ProductService productService = new ProductService();
    private static final MenuService menuService = new MenuService();
    private static final TableService tableService = new TableService();
    private static final OrderService orderService = new OrderService();
    private static final CommandService commandService = new CommandService();

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
                case "Cliente" -> customer();
                case "Funcionário" -> employee();
                case "Produto" -> product();
                case "Menu" -> menu();
                case "Mesa" -> table();
                case "Pedido" -> order();
                case "Comanda" -> command();
            }

        } while (JOptionPane.showConfirmDialog(null, "Deseja continuar?") == JOptionPane.YES_OPTION);
    }

    public static void customer() {
        String[] customerOptions = {"findById", "findAll", "save", "update", "delete", "addAllergy", "deleteAllergy"};

        String selectedCustomerOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                customerOptions,
                null
        );

        switch (selectedCustomerOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(customerService.findById(id));
            }

            case "findAll" -> System.out.println(customerService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    System.out.println("Por favor crie um funcionário antes de criar um cliente");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira um nome:");
                var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:");
                var dayOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o dia do nascimento:"));
                var monthOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o mês do nascimento:"));
                var yearOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o ano do nascimento:"));
                var cpf = JOptionPane.showInputDialog("Insira o cpf:");
                var country = JOptionPane.showInputDialog("Insira o país:");
                var state = JOptionPane.showInputDialog("Insira o estado:");
                var city = JOptionPane.showInputDialog("Insira a cidade:");
                var zipCode = JOptionPane.showInputDialog("Insira o CEP:");
                var neighborhood = JOptionPane.showInputDialog("Insira o bairro:");
                var street = JOptionPane.showInputDialog("Insira a rua:");
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está criando esse cliente?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                customerService.save(
                        new Customer(
                                name,
                                phoneNumber,
                                LocalDate.of(yearOfBirthDate, monthOfBirthDate, dayOfBirthDate),
                                cpf,
                                new Address(country, state, city, zipCode, neighborhood, street),
                                employeeService.findById((int) createdBy)
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                customerService.delete(customerService.findById(id));
            }

            case "addAllergy" -> {
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (customers.isEmpty()) {
                    System.out.println("Por favor crie um cliente antes de adicionar uma alergia a um cliente");
                    break;
                }

                var allergy = (Allergy) JOptionPane.showInputDialog(
                        null,
                        "Escolha uma alergia",
                        "Alergias",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Allergy.values(),
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cliente:",
                        "Cliente",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var customer = customerService.findById((int) selectedCustomer);

                customerService.addAllergy(customer, allergy);
            }

            case "deleteAllergy" -> {
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (customers.isEmpty()) {
                    System.out.println("Por favor crie um cliente antes de remover uma alergia a um cliente");
                    break;
                }

                var allergy = (Allergy) JOptionPane.showInputDialog(
                        null,
                        "Escolha uma alergia",
                        "Alergias",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Allergy.values(),
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cliente:",
                        "Cliente",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var customer = customerService.findById((int) selectedCustomer);

                customerService.deleteAllergy(customer, allergy);
            }
        }
    }

    public static void employee() {
        String[] employeeOptions = {"findById", "findAll", "save", "saveAdmin", "update", "delete"};

        String selectedEmployeeOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Funcionário",
                JOptionPane.QUESTION_MESSAGE,
                null,
                employeeOptions,
                null
        );

        switch (selectedEmployeeOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(employeeService.findById(id));
            }

            case "findAll" -> System.out.println(employeeService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    System.out.println("Por favor crie um funcionário admin antes de criar um funcionário");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira um nome:");
                var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:");
                var dayOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o dia do nascimento:"));
                var monthOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o mês do nascimento:"));
                var yearOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o ano do nascimento:"));
                var cpf = JOptionPane.showInputDialog("Insira o cpf:");
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está criando esse funcionário?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var country = JOptionPane.showInputDialog("Insira o país:");
                var state = JOptionPane.showInputDialog("Insira o estado:");
                var city = JOptionPane.showInputDialog("Insira a cidade:");
                var zipCode = JOptionPane.showInputDialog("Insira o CEP:");
                var neighborhood = JOptionPane.showInputDialog("Insira o bairro:");
                var street = JOptionPane.showInputDialog("Insira a rua:");
                var rg = JOptionPane.showInputDialog("Insira o rg:");
                var maritalStatus = (MaritalStatus) JOptionPane.showInputDialog(
                        null,
                        "Insira um estado civil:",
                        "Estado civil",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        MaritalStatus.values(),
                        null
                );
                var educationLevel = (EducationLevel) JOptionPane.showInputDialog(
                        null,
                        "Insira um nível de educação:",
                        "Nível de educação",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        EducationLevel.values(),
                        null
                );
                var position = (Position) JOptionPane.showInputDialog(
                        null,
                        "Insira um cargo:",
                        "Cargo",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Position.values(),
                        null
                );
                var workCardNumber = JOptionPane.showInputDialog("Insira o número da carteira de trabalho:");

                employeeService.save(
                        new Employee(
                                name,
                                phoneNumber,
                                LocalDate.of(yearOfBirthDate, monthOfBirthDate, dayOfBirthDate),
                                cpf,
                                new Address(country, state, city, zipCode, neighborhood, street),
                                employeeService.findById((int) createdBy),
                                rg,
                                maritalStatus,
                                educationLevel,
                                position,
                                workCardNumber
                        )
                );
            }

            case "saveAdmin" -> {
                if (JOptionPane.showConfirmDialog(null, "Se deseja adicionar um admin padrão?") == JOptionPane.YES_OPTION) {
                    employeeService.save(
                            new Employee(
                                    "Eduardo",
                                    "47999197929",
                                    LocalDate.of(2003, 11, 27),
                                    "11111111111",
                                    new Address("Brasil", "SC", "Blumenau", "89022110", "Velha", "Rua dos caçadores"),
                                    null,
                                    "123123123",
                                    MaritalStatus.SINGLE,
                                    EducationLevel.HIGHER_EDUCATION,
                                    Position.MANAGER,
                                    "123123123"
                            )
                    );
                } else {
                    var name = JOptionPane.showInputDialog("Insira um nome:");
                    var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:");
                    var dayOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o dia do nascimento:"));
                    var monthOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o mês do nascimento:"));
                    var yearOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o ano do nascimento:"));
                    var cpf = JOptionPane.showInputDialog("Insira o cpf:");
                    var country = JOptionPane.showInputDialog("Insira o país:");
                    var state = JOptionPane.showInputDialog("Insira o estado:");
                    var city = JOptionPane.showInputDialog("Insira a cidade:");
                    var zipCode = JOptionPane.showInputDialog("Insira o CEP:");
                    var neighborhood = JOptionPane.showInputDialog("Insira o bairro:");
                    var street = JOptionPane.showInputDialog("Insira a rua:");
                    var rg = JOptionPane.showInputDialog("Insira o rg:");
                    var maritalStatus = (MaritalStatus) JOptionPane.showInputDialog(
                            null,
                            "Insira um estado civil:",
                            "Estado civil",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            MaritalStatus.values(),
                            null
                    );
                    var educationLevel = (EducationLevel) JOptionPane.showInputDialog(
                            null,
                            "Insira um nível de educação:",
                            "Nível de educação",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            EducationLevel.values(),
                            null
                    );
                    var position = (Position) JOptionPane.showInputDialog(
                            null,
                            "Insira um cargo:",
                            "Cargo",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            Position.values(),
                            null
                    );
                    var workCardNumber = JOptionPane.showInputDialog("Insira o número da carteira de trabalho:");

                    employeeService.save(
                            new Employee(
                                    name,
                                    phoneNumber,
                                    LocalDate.of(yearOfBirthDate, monthOfBirthDate, dayOfBirthDate),
                                    cpf,
                                    new Address(country, state, city, zipCode, neighborhood, street),
                                    null,
                                    rg,
                                    maritalStatus,
                                    educationLevel,
                                    position,
                                    workCardNumber
                            )
                    );
                }
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                employeeService.delete(employeeService.findById(id));
            }
        }
    }

    public static void product() {
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
                    System.out.println("Insira um funcionário antes de inserir um produto");
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
                var createdByEmployee = employeeService.findById((int) createdBy);

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

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                productService.delete(productService.findById(id));
            }
        }
    }

    public static void menu() {
        String[] menuOptions = {"findById", "findAll", "save", "update", "delete", "addProduct", "deleteProduct"};

        String selectedMenuOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Menu",
                JOptionPane.QUESTION_MESSAGE,
                null,
                menuOptions,
                null
        );

        switch (selectedMenuOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(menuService.findById(id));
            }

            case "findAll" -> System.out.println(menuService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    System.out.println("Por favor crie um funcionário antes de criar um cliente");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira o nome do cardápio:");
                var menuCode = JOptionPane.showInputDialog("Insira o código do cardápio:");
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando esse cardápio?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy);

                menuService.save(
                        new Menu(
                                name,
                                menuCode,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                menuService.delete(menuService.findById(id));
            }

            case "addProduct" -> {
                var menus = menuService.findAll();
                var menuIds = menus.stream().map(Menu::getId).toArray();
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();

                if (menus.isEmpty() || products.isEmpty()) {
                    System.out.println("Por favor crie um cardápio e um produto antes de adicionar um produto a um cardápio");
                    break;
                }

                var selectedMenu = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cardápio:",
                        "Cardápios",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        menuIds,
                        null
                );
                var selectedProduct = JOptionPane.showInputDialog(
                        null,
                        "Escolha um produto",
                        "Produtos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        null
                );

                var menu = menuService.findById((int) selectedMenu);
                var product = productService.findById((int) selectedProduct);

                menuService.addProduct(menu, product);
            }

            case "deleteProduct" -> {
                var menus = menuService.findAll();
                var menuIds = menus.stream().map(Menu::getId).toArray();
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();

                if (menus.isEmpty() || products.isEmpty()) {
                    System.out.println("Por favor crie um cardápio e um produto antes de adicionar um produto a um cardápio");
                    break;
                }

                var selectedMenu = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cardápio:",
                        "Cardápios",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        menuIds,
                        null
                );
                var selectedProduct = JOptionPane.showInputDialog(
                        null,
                        "Escolha um produto",
                        "Produtos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        null
                );

                var menu = menuService.findById((int) selectedMenu);
                var product = productService.findById((int) selectedProduct);

                menuService.deleteProduct(menu, product);
            }
        }
    }

    public static void table() {
        String[] tableOptions = {"findById", "findAll", "save", "update", "delete", "addCustomer", "deleteCustomer"};

        String selectedTableOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos da mesa",
                JOptionPane.QUESTION_MESSAGE,
                null,
                tableOptions,
                null
        );

        switch (selectedTableOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(tableService.findById(id));
            }

            case "findAll" -> System.out.println(tableService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    System.out.println("Por favor crie um funcionário antes de criar um cliente");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira um nome para a mesa:");
                var tableNumber = Short.parseShort(JOptionPane.showInputDialog("Insira um número para a mesa:"));
                var maxCapacity = Short.parseShort(JOptionPane.showInputDialog("Insira a capacidade máxima da mesa:"));
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando esse cardápio?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy);

                tableService.save(
                        new Table(
                                name,
                                tableNumber,
                                maxCapacity,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                tableService.delete(tableService.findById(id));
            }

            case "addCustomer" -> {
                var tables = tableService.findAll();
                var tableIds = tables.stream().map(Table::getId).toArray();
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (tables.isEmpty() || customers.isEmpty()) {
                    System.out.println("Por favor crie uma mesa e um cliente antes de adicionar um cliente a uma mesa");
                    break;
                }

                var selectedTable = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma mesa:",
                        "Mesas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tableIds,
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Escolha um cliente",
                        "Clientes",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var table = tableService.findById((int) selectedTable);
                var customer = customerService.findById((int) selectedCustomer);

                tableService.addCustomer(table, customer);
            }

            case "deleteCustomer" -> {
                var tables = tableService.findAll();
                var tableIds = tables.stream().map(Table::getId).toArray();
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (tables.isEmpty() || customers.isEmpty()) {
                    System.out.println("Por favor crie uma mesa e um cliente antes de deletar um cliente de uma mesa");
                    break;
                }

                var selectedTable = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma mesa:",
                        "Mesas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tableIds,
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Escolha um cliente",
                        "Clientes",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var table = tableService.findById((int) selectedTable);
                var customer = customerService.findById((int) selectedCustomer);

                tableService.deleteCustomer(table, customer);
            }
        }
    }

    public static void order() {
        String[] orderOptions = {"findById", "findAll", "save", "update", "delete", "addNote", "updateNote", "deleteNote"};

        String selectedOrderOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do pedido",
                JOptionPane.QUESTION_MESSAGE,
                null,
                orderOptions,
                null
        );

        switch (selectedOrderOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(orderService.findById(id));
            }

            case "findAll" -> System.out.println(orderService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();

                if (employees.isEmpty() || products.isEmpty()) {
                    System.out.println("Por favor crie um funcionário e um produto antes de criar um pedido");
                    break;
                }

                var selectedProduct = JOptionPane.showInputDialog(
                        null,
                        "Selecione um produto:",
                        "Produto",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        null
                );
                var selectedEmployee = JOptionPane.showInputDialog(
                        null,
                        "Selecione um funcionário:",
                        "Funcionário",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var product = productService.findById((int) selectedProduct);
                var employee = employeeService.findById((int) selectedEmployee);
                var quantity = Integer.parseInt(JOptionPane.showInputDialog("Insira a quantidade:"));
                var status = (ProductStatus) JOptionPane.showInputDialog(
                        null,
                        "Selecione um status:",
                        "Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        ProductStatus.values(),
                        null
                );
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando esse cardápio?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy);

                orderService.save(
                        new Order(
                                product,
                                employee,
                                quantity,
                                status,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                orderService.delete(orderService.findById(id));
            }

            case "addNote" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (orders.isEmpty()) {
                    System.out.println("Por favor crie um pedido antes de adicionar uma observação a um pedido");
                    break;
                }

                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido:",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );
                var note = JOptionPane.showInputDialog("Insira uma observação ao pedido");

                var order = orderService.findById((int) selectedOrder);

                orderService.addNote(order, note);
            }

            case "updateNote" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (orders.isEmpty()) {
                    System.out.println("Por favor crie um pedido antes de atualizar uma observação a um pedido");
                    break;
                }

                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido:",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var order = orderService.findById((int) selectedOrder);

                var note = (String) JOptionPane.showInputDialog(
                        null,
                        "Selecione uma observação para atualizar:",
                        "Observações",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        order.getNotes().toArray(),
                        null
                );

                var updatedNote = JOptionPane.showInputDialog("Insira a atualização desejada");

                orderService.updateNote(order, note, updatedNote);
            }

            case "deleteNote" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (orders.isEmpty()) {
                    System.out.println("Por favor crie um pedido antes de remover uma observação de um pedido");
                    break;
                }

                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido:",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var order = orderService.findById((int) selectedOrder);

                var note = (String) JOptionPane.showInputDialog(
                        null,
                        "Selecione uma observação para deletar:",
                        "Observações",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        order.getNotes().toArray(),
                        null
                );

                orderService.deleteNote(order, note);
            }
        }
    }

    public static void command() {
        String[] orderOptions = {"findById", "findAll", "save", "update", "delete", "addOrder", "deleteOrder", "closeCommand"};

        String selectedOrderOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos da comanda",
                JOptionPane.QUESTION_MESSAGE,
                null,
                orderOptions,
                null
        );

        switch (selectedOrderOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(commandService.findById(id));
            }

            case "findAll" -> System.out.println(commandService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();
                var tables = tableService.findAll();
                var tableIds = tables.stream().map(Table::getId).toArray();

                if (employees.isEmpty() || customers.isEmpty() || tables.isEmpty()) {
                    System.out.println("Por favor crie um funcionário e um cliente e uma mesa antes de criar um pedido");
                    break;
                }

                var selectedTable = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma mesa:",
                        "Mesa",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tableIds,
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cliente:",
                        "Cliente",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var table = tableService.findById((int) selectedTable);
                var customer = customerService.findById((int) selectedCustomer);

                var status = (OrderStatus) JOptionPane.showInputDialog(
                        null,
                        "Selecione um status:",
                        "Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        OrderStatus.values(),
                        null
                );

                var totalPrice = Double.parseDouble(JOptionPane.showInputDialog("Insira o preço total:"));
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando essa comanda?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy);

                commandService.save(
                        new Command(
                                table,
                                customer,
                                status,
                                totalPrice,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                commandService.delete(commandService.findById(id));
            }

            case "addOrder" -> {
                var commands = commandService.findAll();
                var commandIds = commands.stream().map(Command::getId).toArray();
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (commands.isEmpty() || orders.isEmpty()) {
                    System.out.println("Por favor crie uma comanda e um pedido antes de adicionar um pedido a uma comanda");
                    break;
                }

                var selectedCommand = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma comanda:",
                        "Comandas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        commandIds,
                        null
                );
                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var command = commandService.findById((int) selectedCommand);
                var order = orderService.findById((int) selectedOrder);

                commandService.addOrder(command, order);
            }

            case "deleteOrder" -> {
                var commands = commandService.findAll();
                var commandIds = commands.stream().map(Command::getId).toArray();
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (commands.isEmpty() || orders.isEmpty()) {
                    System.out.println("Por favor crie uma comanda e um pedido antes de deletar um pedido de uma comanda");
                    break;
                }

                var selectedCommand = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma comanda:",
                        "Comandas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        commandIds,
                        null
                );
                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var command = commandService.findById((int) selectedCommand);
                var order = orderService.findById((int) selectedOrder);

                commandService.deleteOrder(command, order);
            }

            case "closeCommand" -> {
                var commands = commandService.findAll();
                var commandIds = commands.stream().map(Command::getId).toArray();

                if (commands.isEmpty()) {
                    System.out.println("Por favor crie uma comanda antes de fechar uma comanda");
                    break;
                }

                var selectedCommand = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma comanda:",
                        "Comandas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        commandIds,
                        null
                );
                var cash = Double.parseDouble(JOptionPane.showInputDialog("Insira um valor"));

                var command = commandService.findById((int) selectedCommand);

                commandService.closeCommand(command, cash);
            }
        }
    }
}