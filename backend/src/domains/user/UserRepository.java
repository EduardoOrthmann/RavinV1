package domains.user;

import database.Query;
import enums.Role;
import interfaces.Repository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository implements Repository<User> {
    private static final String TABLE_NAME = Constants.USER_TABLE;
    private final DatabaseConnector databaseConnector;

    public UserRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Override
    public User save(User entity) {
        String query = new Query()
                .insert(TABLE_NAME, "username", "password", "role")
                .values("?", "?", "CAST(? AS ROLE)")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(query, entity.getUsername(), entity.getPassword(), entity.getRole().toString())) {
            if (resultSet.next()) {
                return mapResultSetToEntity(resultSet);
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public void update(User entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("username", "?")
                .set("password", "?")
                .set("role", "CAST(? AS ROLE)")
                .set("token", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(query, entity.getUsername(), entity.getPassword(), entity.getRole().toString(), entity.getToken(), entity.getId());
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(User entity) {
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
    public User mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                Role.valueOf(resultSet.getString("role")),
                resultSet.getString("token")
        );
    }

    public Optional<User> findByUsername(String username) {
        return findByColumn("username", username);
    }

    public Optional<User> findByToken(String token) {
        return findByColumn("token", token);
    }

    private Optional<User> findByColumn(String columnName, String columnValue) {
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where(columnName, "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, columnValue)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return Optional.empty();
    }
}
