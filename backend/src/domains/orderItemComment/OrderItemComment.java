package domains.orderItemComment;

public class OrderItemComment {
    private int id;
    private int orderItemId;
    private String comment;

    public OrderItemComment(int id, int orderItemId, String comment) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.comment = comment;
    }

    public OrderItemComment(int orderItemId, String comment) {
        this.orderItemId = orderItemId;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
