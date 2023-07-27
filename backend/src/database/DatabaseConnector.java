package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    public DatabaseConnector(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }

        try {
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            System.out.println("Conectado ao banco de dados");
        } catch (SQLException e) {
            throw new SQLException("Não foi possível conectar ao banco de dados\n\n" + e.getMessage());
        }
    }

    public void disconnect() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            return;
        }

        try {
            this.connection.close();
            System.out.println("Desconectado do banco de dados");
        } catch (SQLException e) {
            throw new SQLException("Não foi possível desconectar do banco de dados\n\n" + e.getMessage());
        }
    }

    public Connection getConnection() {
        return this.connection;
    }
}
