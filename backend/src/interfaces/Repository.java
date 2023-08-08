package interfaces;

import database.Query;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    default Optional<T> findById(int id) {
        String query = new Query()
                .select("*")
                .from(getTableName())
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = getDatabaseConnector().connect();
             ResultSet resultSet = connector.executeQuery(query, id)) {
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return Optional.empty();
    }

    default List<T> findAll() {
        List<T> entities = new ArrayList<>();
        String query = new Query()
                .select("*")
                .from(getTableName())
                .build();

        try (DatabaseConnector connector = getDatabaseConnector().connect();
             ResultSet resultSet = connector.executeQuery(query)) {
            while (resultSet.next()) {
                entities.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return entities;
    }

    T save(T entity);
    void update(T entity);
    void delete(T entity);

    String getTableName();
    DatabaseConnector getDatabaseConnector();
    T mapResultSetToEntity(ResultSet resultSet) throws SQLException;
}
