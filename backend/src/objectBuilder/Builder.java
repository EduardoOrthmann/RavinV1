package objectBuilder;

import domains.address.Address;
import domains.customer.Customer;
import domains.customer.CustomerService;
import domains.employee.Employee;
import domains.employee.EmployeeService;
import domains.menu.Menu;
import domains.menu.MenuService;
import domains.product.Product;
import domains.product.ProductService;
import domains.table.Table;
import domains.table.TableService;
import domains.user.User;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;
import enums.Role;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class Builder {
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final TableService tableService;
    private final ProductService productService;
    private final MenuService menuService;

    public Builder(EmployeeService employeeService, CustomerService customerService, TableService tableService, ProductService productService, MenuService menuService) {
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.tableService = tableService;
        this.productService = productService;
        this.menuService = menuService;
    }

    public void employeeBuilder() {
        employeeService.save(
                new Employee(
                        "João Silva",
                        "11987654321",
                        LocalDate.of(1990, 5, 15),
                        "123456789",
                        new Address("Brazil", "São Paulo", "São Paulo", "01234-567", "Centro", "Rua Principal"),
                        new User("joao.silva", "123456", Role.ADMIN),
                        null,
                        "12345678",
                        MaritalStatus.SINGLE,
                        EducationLevel.HIGHER_EDUCATION,
                        Position.WAITER,
                        "A12345678"
                )
        );
        employeeService.save(
                new Employee(
                        "Maria Santos",
                        "11987654321",
                        LocalDate.of(1992, 8, 25),
                        "987654321",
                        new Address("Brazil", "Rio de Janeiro", "Rio de Janeiro", "02345-678", "Centro", "Rua Principal"),
                        new User("maria.santos", "654321", Role.MANAGER),
                        null,
                        "87654321",
                        MaritalStatus.MARRIED,
                        EducationLevel.MASTERS_EDUCATION,
                        Position.MANAGER,
                        "B67890123"
                )
        );
        employeeService.save(
                new Employee(
                        "Pedro Oliveira",
                        "11987654321",
                        LocalDate.of(1985, 10, 8),
                        "555123456",
                        new Address("Brazil", "Belo Horizonte", "Minas Gerais", "03456-789", "Centro", "Rua Principal"),
                        new User("pedro.oliveira", "123456", Role.EMPLOYEE),
                        null,
                        "444123456",
                        MaritalStatus.MARRIED,
                        EducationLevel.HIGHER_EDUCATION,
                        Position.COOK,
                        "C54321098"
                )
        );
        employeeService.save(
                new Employee(
                        "Ana Costa",
                        "11987654321",
                        LocalDate.of(1993, 4, 20),
                        "111222333",
                        new Address("Brazil", "Salvador", "Bahia", "04567-890", "Centro", "Rua Principal"),
                        new User("ana.costa", "123456", Role.ADMIN),
                        null,
                        "987654321",
                        MaritalStatus.SINGLE,
                        EducationLevel.SECONDARY_EDUCATION,
                        Position.CASHIER,
                        "D98765432"
                )
        );
        employeeService.save(
                new Employee(
                        "Lucas Pereira",
                        "11987654321",
                        LocalDate.of(1991, 12, 1),
                        "456456456",
                        new Address("Brazil", "Recife", "Pernambuco", "05678-901", "Centro", "Rua Principal"),
                        new User("lucas.pereira", "123456", Role.ADMIN),
                        null,
                        "789789789",
                        MaritalStatus.SINGLE,
                        EducationLevel.HIGHER_EDUCATION,
                        Position.HOST,
                        "E34567890"
                )
        );
    }

    public void customerBuilder() {
        customerService.save(
                new Customer(
                        "John Smith",
                        "11987654321",
                        LocalDate.of(1990, 5, 15),
                        "123456789",
                        new Address("Brazil", "São Paulo", "São Paulo", "01234-567", "Centro", "Rua Principal"),
                        new User("johnsmith", "1234", Role.CUSTOMER),
                        null
                )
        );
        customerService.save(
                new Customer(
                        "Mary Johnson",
                        "11987654321",
                        LocalDate.of(1992, 8, 25),
                        "987654321",
                        new Address("Brazil", "Rio de Janeiro", "Rio de Janeiro", "02345-678", "Centro", "Rua Principal"),
                        new User("maryjohnson", "1234", Role.CUSTOMER),
                        null
                )
        );
        customerService.save(
                new Customer(
                        "Peter Oliveira",
                        "11987654321",
                        LocalDate.of(1985, 10, 8),
                        "555123456",
                        new Address("Brazil", "Belo Horizonte", "Minas Gerais", "03456-789", "Centro", "Rua Principal"),
                        new User("peteroliveira", "1234", Role.CUSTOMER),
                        null
                )
        );
        customerService.save(
                new Customer(
                        "Anna Costa",
                        "11987654321",
                        LocalDate.of(1993, 4, 20),
                        "111222333",
                        new Address("Brazil", "Salvador", "Bahia", "04567-890", "Centro", "Rua Principal"),
                        new User("annacosta", "1234", Role.CUSTOMER),
                        null
                )
        );
        customerService.save(
                new Customer(
                        "Lucas Pereira",
                        "11987654321",
                        LocalDate.of(1991, 12, 1),
                        "456456456",
                        new Address("Brazil", "Recife", "Pernambuco", "05678-901", "Centro", "Rua Principal"),
                        new User("lucaspereira", "1234", Role.CUSTOMER),
                        null
                )
        );
    }

    public void tableBuilder() {
        tableService.save(new Table("Table 1", (short) 1, (short) 4, null));
        tableService.save(new Table("Table 2", (short) 2, (short) 6, null));
        tableService.save(new Table("Table 3", (short) 3, (short) 2, null));
        tableService.save(new Table("Table 4", (short) 4, (short) 8, null));
        tableService.save(new Table("Table 5", (short) 5, (short) 3, null));
    }

    public void productBuilder() {
        // main dishes
        productService.save(
                new Product(
                        "Hambúrguer",
                        "Delicioso hambúrguer de carne",
                        "H001",
                        5.99,
                        9.99,
                        LocalTime.of(0, 15),
                        null
                )
        );
        productService.save(
                new Product(
                        "Pizza",
                        "Pizza de queijo irresistível",
                        "P002",
                        7.99,
                        12.99,
                        LocalTime.of(0, 30),
                        null
                )
        );
        productService.save(
                new Product(
                        "Massa",
                        "Prato de massa saboroso",
                        "M004", 6.99,
                        11.99,
                        LocalTime.of(0, 20),
                        null
                )
        );
        productService.save(
                new Product(
                        "Sopa",
                        "Sopa quente e reconfortante",
                        "S007",
                        3.99,
                        6.99,
                        LocalTime.of(0, 10),
                        null
                )
        );
        // natural
        productService.save(
                new Product(
                        "Salada",
                        "Salada fresca e saudável",
                        "S003",
                        3.99,
                        6.99,
                        LocalTime.of(0, 10),
                        null
                )
        );
        productService.save(
                new Product(
                        "Sanduíche",
                        "Clássico sanduíche de presunto e queijo",
                        "S005",
                        4.99,
                        8.99,
                        LocalTime.of(0, 10),
                        null
                )
        );
        // starters
        productService.save(
                new Product(
                        "Bife",
                        "Bife grelhado suculento",
                        "B006",
                        12.99,
                        19.99,
                        LocalTime.of(0, 25),
                        null
                )
        );
        productService.save(
                new Product(
                        "Peixe e Batatas",
                        "Clássico peixe e batatas britânico",
                        "P008",
                        8.99,
                        14.99,
                        LocalTime.of(0, 20),
                        null
                )
        );
        productService.save(
                new Product(
                        "Sushi",
                        "Rolinhos de sushi frescos e deliciosos",
                        "S009",
                        10.99,
                        16.99,
                        LocalTime.of(0, 15),
                        null
                )
        );
        // desserts
        productService.save(
                new Product(
                        "Sorvete",
                        "Sorvete cremoso e refrescante",
                        "S010",
                        3.99,
                        7.99,
                        LocalTime.of(0, 5),
                        null
                )
        );
        productService.save(
                new Product(
                        "Bolo de Chocolate",
                        "Bolo de chocolate delicioso",
                        "BC011",
                        4.99,
                        9.99,
                        LocalTime.of(0, 5),
                        null
                )
        );
        // drinks
        productService.save(
                new Product(
                        "Limonada Refrescante",
                        "Limonada deliciosa e refrescante",
                        "LR001",
                        2.99,
                        4.99,
                        LocalTime.of(0, 3),
                        null
                )
        );
        productService.save(
                new Product(
                        "Suco de Laranja",
                        "Suco de laranja fresco e saudável",
                        "SL002",
                        2.99,
                        4.99,
                        LocalTime.of(0, 3),
                        null
                )
        );
    }

    public void menuBuilder() {
        menuService.save(
                new Menu(
                        "Bebidas",
                        "B001",
                        Set.of(
                                productService.findById(12),
                                productService.findById(13)
                        ),
                        null
                )
        );
        menuService.save(
                new Menu(
                        "Sobremesas",
                        "S002",
                        Set.of(
                                productService.findById(10),
                                productService.findById(11)
                        ),
                        null
                )
        );
        menuService.save(
                new Menu(
                        "Pratos Principais",
                        "P003",
                        Set.of(
                                productService.findById(1),
                                productService.findById(2),
                                productService.findById(3),
                                productService.findById(4)
                        ),
                        null
                )
        );
        menuService.save(
                new Menu(
                        "Entradas",
                        "E004",
                        Set.of(
                                productService.findById(7),
                                productService.findById(8),
                                productService.findById(9)
                        ),
                        null
                )
        );
        menuService.save(
                new Menu(
                        "Naturais",
                        "P005",
                        Set.of(
                                productService.findById(5),
                                productService.findById(6)
                        ),
                        null
                )
        );
    }
}
