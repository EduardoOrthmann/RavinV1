package domains.reservedTableCustomer;

import database.Query;
import domains.customer.Customer;
import domains.customer.CustomerService;
import interfaces.Repository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservedTableCustomerRepository implements Repository<ReservedTableCustomer> {
    private static final String TABLE_NAME = Constants.RESERVED_TABLE_CUSTOMER_TABLE;
    private final DatabaseConnector databaseConnector;
    private final CustomerService customerService;

    public ReservedTableCustomerRepository(DatabaseConnector databaseConnector, CustomerService customerService) {
        this.databaseConnector = databaseConnector;
        this.customerService = customerService;
    }

    @Override
    public ReservedTableCustomer save(ReservedTableCustomer entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "reserved_table_id",
                        "customer_id"
                )
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getReservedTableId(),
                     entity.getCustomer().getId()

             )) {
            if (resultSet.next()) {
                return mapResultSetToEntity(resultSet);
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public void update(ReservedTableCustomer entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("reserved_table_id", "?")
                .set("customer_id", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getReservedTableId(),
                    entity.getCustomer().getId(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(ReservedTableCustomer entity) {
        String query = new Query()
                .delete()
                .from(TABLE_NAME)
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(query, entity.getId());
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    @Override
    public ReservedTableCustomer mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Customer customer = customerService.findById(resultSet.getInt("customer_id"));

        return new ReservedTableCustomer(
                resultSet.getInt("id"),
                resultSet.getInt("reserved_table_id"),
                customer
        );
    }

    public List<Customer> findCustomersByReservedTableId(int reservedTableId) {
        List<Customer> customers = new ArrayList<>();
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("reserved_table_id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, reservedTableId)) {
            while (resultSet.next()) {
                customers.add(mapResultSetToEntity(resultSet).getCustomer());
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return customers;
    }

    public List<Integer> findReservedTableIdByCustomerId(int customerId) {
        List<Integer> reservedTableIds = new ArrayList<>();
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("customer_id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, customerId)) {
            while (resultSet.next()) {
                reservedTableIds.add(mapResultSetToEntity(resultSet).getReservedTableId());
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return reservedTableIds;
    }
}
