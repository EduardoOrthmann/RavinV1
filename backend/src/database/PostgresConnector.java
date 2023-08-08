package database;

import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.*;

public class PostgresConnector implements DatabaseConnector {
    private Connection connection;

    @Override
    public DatabaseConnector connect() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            return this;
        }

        try {
            this.connection = DriverManager.getConnection(Constants.DATABASE_URL, Constants.DATABASE_USERNAME, Constants.DATABASE_PASSWORD);
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_CONNECTION_ERROR + "\n\n" + e.getMessage());
        }

        return this;
    }

    @Override
    public void disconnect() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            return;
        }

        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println(Constants.DATABASE_CONNECTION_ERROR + "\n\n" + e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        this.disconnect();
    }

    public Connection getConnection() {
        return connection;
    }

    public ResultSet executeQuery(String sqlQuery, Object... parameters) throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            throw new IllegalStateException(Constants.DATABASE_UNABLE_TO_EXECUTE_QUERY);
        }

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }

        return preparedStatement.executeQuery();
    }

    public ResultSet executeUpdate(String sqlQuery, Object... parameters) throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            throw new IllegalStateException(Constants.DATABASE_UNABLE_TO_EXECUTE_QUERY);
        }

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }

        preparedStatement.executeUpdate();

        return preparedStatement.getGeneratedKeys();
    }
}
