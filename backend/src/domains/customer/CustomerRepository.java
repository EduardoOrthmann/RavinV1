package domains.customer;

import database.Query;
import domains.address.Address;
import domains.user.User;
import domains.user.UserService;
import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import interfaces.PersonRepository;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerRepository extends AbstractRepository<Customer> implements PersonRepository<Customer> {
    private static final String TABLE_NAME = Constants.CUSTOMER_TABLE;
    private final UserService userService;
    private final DatabaseConnector databaseConnector;

    public CustomerRepository(DatabaseConnector databaseConnector, UserService userService) {
        this.userService = userService;
        this.databaseConnector = databaseConnector;
    }

    @Override
    public Customer save(Customer entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "name",
                        "phone_number",
                        "birth_date",
                        "cpf",
                        "country",
                        "state",
                        "city",
                        "zip_code",
                        "neighborhood",
                        "street",
                        "user_id",
                        "created_by"
                )
                .autoValues()
                .build();

        User user = userService.save(entity.getUser());

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getName(),
                     entity.getPhoneNumber(),
                     entity.getBirthDate(),
                     entity.getCpf(),
                     entity.getAddress().country(),
                     entity.getAddress().state(),
                     entity.getAddress().city(),
                     entity.getAddress().zipCode(),
                     entity.getAddress().neighborhood(),
                     entity.getAddress().street(),
                     user.getId(),
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
    public void update(Customer entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("name", "?")
                .set("phone_number", "?")
                .set("birth_date", "?")
                .set("cpf", "?")
                .set("country", "?")
                .set("state", "?")
                .set("city", "?")
                .set("zip_code", "?")
                .set("neighborhood", "?")
                .set("street", "?")
                .set("updated_by", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getName(),
                    entity.getPhoneNumber(),
                    entity.getBirthDate(),
                    entity.getCpf(),
                    entity.getAddress().country(),
                    entity.getAddress().state(),
                    entity.getAddress().city(),
                    entity.getAddress().zipCode(),
                    entity.getAddress().neighborhood(),
                    entity.getAddress().street(),
                    entity.getUpdatedBy(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(Customer entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("is_active", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(query, false, entity.getId());
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    protected Customer mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        User user = userService.findById(resultSet.getInt("user_id"));

        return new Customer(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("phone_number"),
                resultSet.getDate("birth_date").toLocalDate(),
                resultSet.getString("cpf"),
                new Address(
                        resultSet.getString("country"),
                        resultSet.getString("state"),
                        resultSet.getString("city"),
                        resultSet.getString("zip_code"),
                        resultSet.getString("neighborhood"),
                        resultSet.getString("street")
                ),
                resultSet.getBoolean("is_active"),
                user,
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime(),
                resultSet.getInt("created_by"),
                resultSet.getInt("updated_by")
        );
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
    public Optional<Customer> findByUserId(int userId) {
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("user_id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, userId)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByCpf(String cpf) {
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("cpf", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, cpf)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return Optional.empty();
    }
}
