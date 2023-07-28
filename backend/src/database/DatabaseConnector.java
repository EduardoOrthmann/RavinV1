package database;

import utils.Constants;

import java.sql.*;

public class DatabaseConnector implements AutoCloseable {
    private Connection connection;

    public DatabaseConnector() {
        try {
            connection = DriverManager.getConnection(Constants.DATABASE_URL, Constants.DATABASE_USERNAME, Constants.DATABASE_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Não foi possível conectar ao banco de dados\n\n" + e.getMessage());
        }
    }

    public void disconnect() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            return;
        }

        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Não foi possível desconectar do banco de dados\n\n" + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws Exception {
        this.disconnect();
    }

    public ResultSet executeQuery(String sqlQuery, Object... parameters) throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            throw new IllegalStateException("Não foi possível executar a query pois a conexão com o banco de dados não está aberta");
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
            throw new IllegalStateException("Não foi possível executar a query pois a conexão com o banco de dados não está aberta");
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
