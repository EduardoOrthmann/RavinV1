package interfaces;

import database.Query;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T> {
    public Optional<T> findById(int id) {
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

    public List<T> findAll() {
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

    public abstract T save(T entity);
    public abstract void update(T entity);
    public abstract void delete(T entity);

    protected abstract String getTableName();
    protected abstract DatabaseConnector getDatabaseConnector();
    protected abstract T mapResultSetToEntity(ResultSet resultSet) throws SQLException;
}
