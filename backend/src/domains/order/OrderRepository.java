package domains.order;

import database.Query;
import domains.orderItem.OrderItem;
import domains.orderItem.OrderItemService;
import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderRepository extends AbstractRepository<Order> {
    private static final String TABLE_NAME = Constants.ORDER_TABLE;
    private final DatabaseConnector databaseConnector;
    private OrderItemService orderItemService;

    public OrderRepository(DatabaseConnector databaseConnector, OrderItemService orderItemService) {
        this.databaseConnector = databaseConnector;
        this.orderItemService = orderItemService;
    }

    public void setOrderItemService(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @Override
    public Order save(Order entity) {
        String query = new Query()
                .insert(
                        TABLE_NAME,
                        "table_id",
                        "customer_id",
                        "is_paid",
                        "total_price",
                        "created_by"
                )
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(
                     query,
                     entity.getTableId(),
                     entity.getCustomerId(),
                     entity.isPaid(),
                     entity.getTotalPrice(),
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
    public void update(Order entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("table_id", "?")
                .set("customer_id", "?")
                .set("is_paid", "?")
                .set("total_price", "?")
                .set("updated_by", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(
                    query,
                    entity.getTableId(),
                    entity.getCustomerId(),
                    entity.isPaid(),
                    entity.getTotalPrice(),
                    entity.getUpdatedBy(),
                    entity.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(Order entity) {
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
    protected Order mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Set<OrderItem> orderItems = new HashSet<>(orderItemService.findAllByOrderId(resultSet.getInt("id")));

        return new Order(
                resultSet.getInt("id"),
                resultSet.getInt("table_id"),
                resultSet.getInt("customer_id"),
                resultSet.getBoolean("is_paid"),
                resultSet.getDouble("total_price"),
                orderItems,
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime(),
                resultSet.getInt("created_by"),
                resultSet.getInt("updated_by")
        );
    }

    public List<Order> findAllByTableAndIsPaid(int tableId, boolean isPaid) {
        List<Order> orders = new ArrayList<>();
        String query = new Query()
                .select("*")
                .from(TABLE_NAME)
                .where("table_id", "=", "?")
                .and("is_paid", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, tableId, isPaid)) {
            while (resultSet.next()) {
                orders.add(mapResultSetToEntity(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return orders;
    }
}
