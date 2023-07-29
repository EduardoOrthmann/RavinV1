package interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseConnector extends AutoCloseable {
    DatabaseConnector connect() throws SQLException;
    void disconnect() throws SQLException;
    ResultSet executeQuery(String sqlQuery, Object... parameters) throws SQLException;
    ResultSet executeUpdate(String sqlQuery, Object... parameters) throws SQLException;
}
