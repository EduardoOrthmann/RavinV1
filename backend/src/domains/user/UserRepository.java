package domains.user;

import database.DatabaseConnector;
import enums.Role;
import interfaces.Crud;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements Crud<User> {
    private static final String TABLE_NAME = "\"user\"";
    private final DatabaseConnector databaseConnector;

    public UserRepository() {
        this.databaseConnector = new DatabaseConnector();
    }

    @Override
    public Optional<User> findById(int id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try (ResultSet resultSet = databaseConnector.executeQuery(query, id)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        try (ResultSet resultSet = databaseConnector.executeQuery(query)) {
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return users;
    }

    @Override
    public User save(User entity) {
        String query = "INSERT INTO " + TABLE_NAME + " (username, password, role) VALUES (?, ?, ?)";

        try (ResultSet resultSet = databaseConnector.executeUpdate(query, entity.getUsername(), entity.getPassword(), entity.getRole())) {
            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public void update(User entity) {
        String query = "UPDATE " + TABLE_NAME + " SET username = ?, password = ?, role = ?, token = ? WHERE id = ?";

        try {
            databaseConnector.executeUpdate(query, entity.getUsername(), entity.getPassword(), entity.getRole(), entity.getToken(), entity.getId());
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(User entity) {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try {
            databaseConnector.executeUpdate(query, entity.getId());
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    public Optional<User> findByUsername(String username) {
        return findByColumn("username", username);
    }

    public Optional<User> findByToken(String token) {
        return findByColumn("token", token);
    }

    private Optional<User> findByColumn(String columnName, String columnValue) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + columnName + " = ?";
        try (ResultSet resultSet = databaseConnector.executeQuery(query, columnValue)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        Role role = Role.valueOf(resultSet.getString("role"));
        String token = resultSet.getString("token");

        return new User(id, username, password, role, token);
    }
}
