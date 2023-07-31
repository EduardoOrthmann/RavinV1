package domains.orderItemComment;

import database.Query;

import interfaces.AbstractRepository;
import interfaces.DatabaseConnector;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemCommentRepository extends AbstractRepository<OrderItemComment> {
    private static final String TABLE_NAME = Constants.ORDER_ITEM_COMMENT_TABLE;
    private final DatabaseConnector databaseConnector;

    public OrderItemCommentRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }


    @Override
    public OrderItemComment save(OrderItemComment entity) {
        String query = new Query()
                .insert(TABLE_NAME, "order_item_id", "comment")
                .autoValues()
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeUpdate(query, entity.getOrderItemId(), entity.getComment())) {
            if (resultSet.next()) {
                return mapResultSetToEntity(resultSet);
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public void update(OrderItemComment entity) {
        String query = new Query()
                .update(TABLE_NAME)
                .set("order_item_id", "?")
                .set("comment", "?")
                .where("id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect()) {
            connector.executeUpdate(query, entity.getOrderItemId(), entity.getComment(), entity.getId());
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_MUTATION_ERROR + ": " + e.getMessage());
        }
    }

    @Override
    public void delete(OrderItemComment entity) {
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
    protected OrderItemComment mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new OrderItemComment(
                resultSet.getInt("id"),
                resultSet.getInt("order_item_id"),
                resultSet.getString("comment")
        );
    }

    public List<String> findCommentByOrderItemId(int orderItemId) {
        List<String> orderItemComments = new ArrayList<>();
        String query = new Query()
                .select("comment")
                .from(TABLE_NAME)
                .where("order_item_id", "=", "?")
                .build();

        try (DatabaseConnector connector = databaseConnector.connect();
             ResultSet resultSet = connector.executeQuery(query, orderItemId)) {
            while (resultSet.next()) {
                orderItemComments.add(mapResultSetToEntity(resultSet).getComment());
            }
        } catch (Exception e) {
            throw new RuntimeException(Constants.DATABASE_QUERY_ERROR + ": " + e.getMessage());
        }

        return orderItemComments;
    }
}
