package domains.product;

import database.Query;
import interfaces.Repository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository implements Repository<Product> {
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
                .set("menu_id", "?")
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
                    entity.getMenuId(),
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
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    @Override
    public Product mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getInt("menu_id"),
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

    public List<Product> findByMenuId(int menuId) {
        List<Product> entities = new ArrayList<>();
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("menu_id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, menuId)) {
            while (resultSet.next()) {
                entities.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return entities;
    }
}
