package utils;

public class Constants {
    // database
    public static final String DATABASE_NAME = "ravin";
    public static final String DATABASE_USERNAME = "postgres";
    public static final String DATABASE_PASSWORD = "postgres";
    public static final int DATABASE_PORT = 5432;
    public static final String DATABASE_URL = "jdbc:postgresql://localhost:" + DATABASE_PORT + "/" + DATABASE_NAME;

    // database tables
    public static final String USER_TABLE = "\"user\"";
    public static final String CUSTOMER_TABLE = "customer";
    public static final String EMPLOYEE_TABLE = "employee";
    public static final String PRODUCT_TABLE = "product";
    public static final String MENU_TABLE = "menu";
    public static final String ORDER_TABLE = "\"order\"";
    public static final String ORDER_ITEM_TABLE = "order_item";
    public static final String ORDER_ITEM_COMMENT_TABLE = "order_item_comment";
    public static final String TABLE_TABLE = "\"table\"";
    public static final String RESERVED_TABLE_TABLE = "reserved_table";
    public static final String RESERVED_TABLE_CUSTOMER_TABLE = "reserved_table_customer";

    // paths
    public static final String USER_PATH = "/user";
    public static final String CUSTOMER_PATH = "/customer";
    public static final String EMPLOYEE_PATH = "/employee";
    public static final String PRODUCT_PATH = "/product";
    public static final String MENU_PATH = "/menu";
    public static final String TABLE_PATH = "/table";
    public static final String RESERVED_TABLE_PATH = "/reserved-table";
    public static final String ORDER_ITEM_PATH = "/order-item";
    public static final String ORDER_PATH = "/order";

    // messages
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado";
    public static final String EMPLOYEE_NOT_FOUND = "Funcionário não encontrado";
    public static final String PRODUCT_NOT_FOUND = "Produto não encontrado";
    public static final String MENU_NOT_FOUND = "Cardápio não encontrado";
    public static final String TABLE_NOT_FOUND = "Mesa não encontrada";
    public static final String RESERVED_TABLE_NOT_FOUND = "Mesa reservada não encontrada";
    public static final String ORDER_ITEM_NOT_FOUND = "Item do pedido não encontrado";
    public static final String ORDER_NOT_FOUND = "Comanda não encontrada";
    public static final String TOKEN_NOT_FOUND = "Token não encontrado";
    public static final String INVALID_REQUEST_METHOD = "Método de requisição inválido";
    public static final String INVALID_REQUEST_ENDPOINT = "Endpoint inválido";
    public static final String SUCCESS_MESSAGE = "Operação realizada com sucesso";
    public static final String USERNAME_ALREADY_EXISTS = "Username já cadastrado";
    public static final String CPF_ALREADY_EXISTS = "Cpf já cadastrado";
    public static final String INACTIVE_CUSTOMER = "Cliente inativo";
    public static final String DATABASE_QUERY_ERROR = "Aconteceu um erro ao buscar os dados do banco de dados";
    public static final String DATABASE_MUTATION_ERROR = "Aconteceu um erro ao inserir os dados no banco de dados";
    public static final String MINIMUM_AGE_NOT_REACHED = "Funcionário deve ser maior de idade";
    public static final String INVALID_PASSWORD = "Senha inválida";
    public static final String ORDER_ITEM_NOT_TAKEN = "Item do pedido não foi pego por nenhum funcionário";
    public static final String ORDER_ITEM_ALREADY_TAKEN = "Item do pedido já foi pego por um funcionário";
    public static final String ORDER_ITEM_ALREADY_DELIVERED_OR_CANCELED = "Item do pedido já foi entregue ou cancelado";
    public static final String ORDER_ITEM_ALREADY_CANCELED = "Item do pedido já foi cancelado";
    public static final String TABLE_OCCUPIED = "Mesa já ocupada ou indisponível";
    public static final String RESERVED_TABLE_OVERLAPPING = "Mesa já reservada para esse horário";
    public static final String RESERVED_TABLE_MAX_CAPACITY = "Mesa reservada excede a capacidade máxima";
    public static final String TABLE_NOT_PAID_YET = "Os pedidos da mesa ainda não foram pagos";
    public static final String TABLE_ALREADY_FREE = "Mesa já está livre";
    public static final String DATABASE_CONNECTION_ERROR = "Aconteceu um erro ao conectar com o banco de dados";
    public static final String DATABASE_UNABLE_TO_EXECUTE_QUERY = "Não foi possível executar a query pois a conexão com o banco de dados não está aberta";

    // others
    public static final int MINIMUM_AGE = 18;
}
