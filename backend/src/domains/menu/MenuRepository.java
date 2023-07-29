package domains.menu;

import database.Query;
import domains.product.Product;
import domains.product.ProductService;
import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class MenuRepository extends AbstractRepository<Menu> {
    private static final String TABLE_NAME = Constants.MENU_TABLE;
    private final DatabaseConnector databaseConnector;
    private final ProductService productService;

    public MenuRepository(DatabaseConnector databaseConnector, ProductService productService) {
        this.databaseConnector = databaseConnector;
        this.productService = productService;
    }

    @Override
    public Menu save(Menu entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "name",
                        "menu_code",
                        "created_by"
                )
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getName(),
                     entity.getMenuCode(),
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
    public void update(Menu entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("name", "?")
                .set("menu_code", "?")
                .set("updated_by", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getName(),
                    entity.getMenuCode(),
                    entity.getUpdatedBy(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(Menu entity) {
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
    protected Menu mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Set<Product> products = new HashSet<>(productService.findByMenuId(resultSet.getInt("id")));

        return new Menu(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                products,
                resultSet.getString("menu_code"),
                resultSet.getBoolean("is_active"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime(),
                resultSet.getInt("created_by"),
                resultSet.getInt("updated_by")
        );
    }
}
