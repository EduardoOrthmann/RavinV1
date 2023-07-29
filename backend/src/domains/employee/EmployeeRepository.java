package domains.employee;

import database.Query;
import domains.address.Address;
import domains.user.User;
import domains.user.UserService;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;
import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import interfaces.PersonRepository;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class EmployeeRepository extends AbstractRepository<Employee> implements PersonRepository<Employee> {
    private static final String TABLE_NAME = Constants.EMPLOYEE_TABLE;
    private final UserService userService;
    private final DatabaseConnector databaseConnector;

    public EmployeeRepository(DatabaseConnector databaseConnector, UserService userService) {
        this.userService = userService;
        this.databaseConnector = databaseConnector;
    }

    @Override
    public Employee save(Employee entity) {
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
                        "created_by",
                        "rg",
                        "marital_status",
                        "education_level",
                        "position",
                        "work_card_number"
                )
                .values("?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "CAST(? AS MARITAL_STATUS)", "CAST(? AS EDUCATION_LEVEL)", "CAST(? AS POSITION)", "?")
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
                     entity.getCreatedBy(),
                     entity.getRg(),
                     entity.getMaritalStatus().toString(),
                     entity.getEducationLevel().toString(),
                     entity.getPosition().toString(),
                     entity.getWorkCardNumber()
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
    public void update(Employee entity) {
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
                .set("rg", "?")
                .set("marital_status", "CAST(? AS MARITAL_STATUS)")
                .set("education_level", "CAST(? AS EDUCATION_LEVEL)")
                .set("position", "CAST(? AS POSITION)")
                .set("work_card_number", "?")
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
                    entity.getRg(),
                    entity.getMaritalStatus().toString(),
                    entity.getEducationLevel().toString(),
                    entity.getPosition().toString(),
                    entity.getWorkCardNumber(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(Employee entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("is_active", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(query, entity.isActive(), entity.getId());
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    protected Employee mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        User user = userService.findById(resultSet.getInt("user_id"));

        return new Employee(
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
                resultSet.getInt("updated_by"),
                resultSet.getString("rg"),
                MaritalStatus.valueOf(resultSet.getString("marital_status")),
                EducationLevel.valueOf(resultSet.getString("education_level")),
                Position.valueOf(resultSet.getString("position")),
                resultSet.getString("work_card_number"),
                resultSet.getDate("admission_date").toLocalDate(),
                resultSet.getDate("resignation_date").toLocalDate(),
                resultSet.getBoolean("is_available")
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
    public Optional<Employee> findByUserId(int userId) {
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
    public Optional<Employee> findByCpf(String cpf) {
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
