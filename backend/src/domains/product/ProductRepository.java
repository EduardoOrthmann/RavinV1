package domains.product;

import database.Query;
import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRepository extends AbstractRepository<Product> {
    private static final String TABLE_NAME = Constants.PRODUCT_TABLE;
    private final DatabaseConnector databaseConnector;

    public ProductRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Override
    public Product save(Product entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "name",
                        "description",
                        "product_code",
                        "cost_price",
                        "sale_price",
                        "preparation_time",
                        "created_by"
                )
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getName(),
                     entity.getDescription(),
                     entity.getProductCode(),
                     entity.getCostPrice(),
                     entity.getSalePrice(),
                     entity.getPreparationTime(),
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
    public void update(Product entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("name", "?")
                .set("description", "?")
                .set("product_code", "?")
                .set("cost_price", "?")
                .set("sale_price", "?")
                .set("preparation_time", "?")
                .set("is_available", "?")
                .set("updated_by", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getName(),
                    entity.getDescription(),
                    entity.getProductCode(),
                    entity.getCostPrice(),
                    entity.getSalePrice(),
                    entity.getPreparationTime(),
                    entity.isAvailable(),
                    entity.getUpdatedBy(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(Product entity) {
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
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    @Override
    protected Product mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("product_code"),
                resultSet.getDouble("cost_price"),
                resultSet.getDouble("sale_price"),
                resultSet.getTime("preparation_time").toLocalTime(),
                resultSet.getBoolean("is_active"),
                resultSet.getBoolean("is_available"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime(),
                resultSet.getInt("created_by"),
                resultSet.getInt("updated_by")
        );
    }
}
