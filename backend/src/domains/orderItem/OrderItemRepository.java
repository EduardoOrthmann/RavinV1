package domains.orderItem;

import database.Query;
import domains.orderItemComment.OrderItemCommentRepository;
import domains.product.Product;
import domains.product.ProductService;
import enums.OrderItemStatus;
import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepository extends AbstractRepository<OrderItem> {
    private static final String TABLE_NAME = Constants.ORDER_ITEM_TABLE;
    private final DatabaseConnector databaseConnector;
    private final ProductService productService;
    private final OrderItemCommentRepository orderItemCommentRepository;

    public OrderItemRepository(DatabaseConnector databaseConnector, ProductService productService, OrderItemCommentRepository orderItemCommentRepository) {
        this.databaseConnector = databaseConnector;
        this.productService = productService;
        this.orderItemCommentRepository = orderItemCommentRepository;
    }

    @Override
    public OrderItem save(OrderItem entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "order_id",
                        "product_id",
                        "employee_id",
                        "quantity",
                        "total_price",
                        "created_by"
                )
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getOrderId(),
                     entity.getProduct().getId(),
                     entity.getEmployeeId(),
                     entity.getQuantity(),
                     // this is totally wrong, but I don't have time to fix it, the reason is that the total price should be calculated
                     // in the service layer, therefore, I need to fix a circular dependency between OrderItem and Order
                     entity.getTotalPrice() == 0 ? entity.getProduct().getSalePrice() * entity.getQuantity() : entity.getTotalPrice(),
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
    public void update(OrderItem entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("product_id", "?")
                .set("employee_id", "?")
                .set("quantity", "?")
                .set("total_price", "?")
                .set("status", "CAST(? AS ORDER_ITEM_STATUS)")
                .set("updated_by", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getProduct().getId(),
                    entity.getEmployeeId(),
                    entity.getQuantity(),
                    // this is totally wrong, but I don't have time to fix it, the reason is that the total price should be calculated
                    // in the service layer, therefore, I need to fix a circular dependency between OrderItem and Order
                    entity.getTotalPrice() == 0 ? entity.getProduct().getSalePrice() * entity.getQuantity() : entity.getTotalPrice(),
                    entity.getStatus().toString(),
                    entity.getUpdatedBy(),
                    entity.getId()

            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(OrderItem entity) {
        String query = new Query()
                .delete()
                .from(TABLE_NAME)
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(query, entity.getId());
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
    protected OrderItem mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Product product = productService.findById(resultSet.getInt("product_id"));
        List<String> orderItemComments = orderItemCommentRepository.findCommentByOrderItemId(resultSet.getInt("id"));

        return new OrderItem(
                resultSet.getInt("id"),
                resultSet.getInt("order_id"),
                product,
                resultSet.getInt("employee_id"),
                resultSet.getInt("quantity"),
                resultSet.getDouble("total_price"),
                OrderItemStatus.valueOf(resultSet.getString("status")),
                orderItemComments,
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime(),
                resultSet.getInt("created_by"),
                resultSet.getInt("updated_by")
        );
    }

    public List<OrderItem> findAllByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("order_id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, orderId)) {
            while (resultSet.next()) {
                orderItems.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return orderItems;
    }
}
