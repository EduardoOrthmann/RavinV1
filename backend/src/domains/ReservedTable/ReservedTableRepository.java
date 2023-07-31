package domains.ReservedTable;

import database.Query;
import domains.customer.Customer;
import domains.reservedTableCustomer.ReservedTableCustomerRepository;
import domains.table.Table;
import domains.table.TableService;
import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReservedTableRepository extends AbstractRepository<ReservedTable> {
    private static final String TABLE_NAME = Constants.RESERVED_TABLE_TABLE;
    private final DatabaseConnector databaseConnector;
    private final TableService tableService;
    private final ReservedTableCustomerRepository reservedTableCustomerRepository;

    public ReservedTableRepository(DatabaseConnector databaseConnector, TableService tableService, ReservedTableCustomerRepository reservedTableCustomerRepository) {
        this.databaseConnector = databaseConnector;
        this.tableService = tableService;
        this.reservedTableCustomerRepository = reservedTableCustomerRepository;
    }

    @Override
    public ReservedTable save(ReservedTable entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "table_id",
                        "number_of_seat",
                        "reserved_start",
                        "reserved_end",
                        "created_by"
                )
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getTable().getId(),
                     entity.getNumberOfPeople(),
                     entity.getReservedAt().start(),
                     entity.getReservedAt().end(),
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
    public void update(ReservedTable entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("table_id", "?")
                .set("number_of_seat", "?")
                .set("reserved_start", "?")
                .set("reserved_end", "?")
                .set("updated_by", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getTable().getId(),
                    entity.getNumberOfPeople(),
                    entity.getReservedAt().start(),
                    entity.getReservedAt().end(),
                    entity.getUpdatedBy(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(ReservedTable entity) {
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
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    @Override
    protected ReservedTable mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Table table = tableService.findById(resultSet.getInt("table_id"));
        Set<Customer> customers = new HashSet<>(reservedTableCustomerRepository.findCustomersByReservedTableId(resultSet.getInt("id")));

        return new ReservedTable(
                resultSet.getInt("id"),
                table,
                customers,
                resultSet.getInt("number_of_seat"),
                new Reservation(
                        resultSet.getTimestamp("reserved_start").toLocalDateTime(),
                        resultSet.getTimestamp("reserved_end").toLocalDateTime()
                ),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime(),
                resultSet.getInt("created_by"),
                resultSet.getInt("updated_by")
        );
    }

    private List<ReservedTable> findAllByColumn(String columnName, int columnValue) {
        List<ReservedTable> reservedTableList = new ArrayList<>();
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where(columnName, "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, columnValue)) {
            while (resultSet.next()) {
                reservedTableList.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return reservedTableList;
    }

    public List<ReservedTable> findByCustomer(int customerId) {
        List<Integer> reservedTableId = reservedTableCustomerRepository.findReservedTableIdByCustomerId(customerId);

        if (reservedTableId.isEmpty()) {
            return Collections.emptyList();
        }

        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("id", "IN", "(?)")
                .build();

        String joinedReservedTableId = reservedTableId.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        List<ReservedTable> reservedTableList = new ArrayList<>();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, joinedReservedTableId)) {
            while (resultSet.next()) {
                reservedTableList.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return reservedTableList;
    }

    public List<ReservedTable> findByTable(int tableId) {
        return findAllByColumn("table_id", tableId);
    }

    public List<ReservedTable> findAllByCustomerAndDateTime(int customerId, LocalDateTime dateTime) {
        List<ReservedTable> reservedTableList = new ArrayList<>();

        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .join(Constants.RESERVED_TABLE_CUSTOMER_TABLE)
                .on(TABLE_NAME + ".id", "=", Constants.RESERVED_TABLE_CUSTOMER_TABLE + ".reserved_table_id")
                .where("customer_id", "=", "?")
                .and("reserved_start", "<=", "?")
                .and("reserved_end", ">=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, customerId, dateTime, dateTime)) {
            while (resultSet.next()) {
                reservedTableList.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return reservedTableList;
    }
}
