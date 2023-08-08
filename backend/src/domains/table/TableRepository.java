package domains.table;

import database.Query;
import domains.customer.Customer;
import domains.customer.CustomerService;
import enums.TableStatus;
import interfaces.Repository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class TableRepository implements Repository<Table> {
    private static final String TABLE_NAME = Constants.TABLE_TABLE;
    private final DatabaseConnector databaseConnector;
    private final CustomerService customerService;

    public TableRepository(DatabaseConnector databaseConnector, CustomerService customerService) {
        this.databaseConnector = databaseConnector;
        this.customerService = customerService;
    }

    @Override
    public Table save(Table entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "name",
                        "table_number",
                        "max_capacity",
                        "created_by"
                )
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getName(),
                     entity.getTableNumber(),
                     entity.getMaxCapacity(),
                     entity.getCreatedBy()
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
    public void update(Table entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("name", "?")
                .set("table_number", "?")
                .set("max_capacity", "?")
                .set("status", "CAST(? AS TABLE_STATUS)")
                .set("updated_by", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getName(),
                    entity.getTableNumber(),
                    entity.getMaxCapacity(),
                    entity.getStatus().toString(),
                    entity.getUpdatedBy(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(Table entity) {
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
    public Table mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Set<Customer> customers = new HashSet<>(customerService.findAllByTableId(resultSet.getInt("id")));

        return new Table(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getShort("table_number"),
                resultSet.getShort("max_capacity"),
                customers,
                TableStatus.valueOf(resultSet.getString("status")),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime(),
                resultSet.getInt("created_by"),
                resultSet.getInt("updated_by")
        );
    }
}
