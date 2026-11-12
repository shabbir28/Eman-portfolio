package model;

public class UserPending {
    private int id; // PendingID
    private int userId; // FK to UserReceived
    private int quantity;
    // Prompt says "edit Quantity in Pending table".
    // "Reason" is in UserReceived.

    public UserPending() {}

    public UserPending(int id, int userId, int quantity) {
        this.id = id;
        this.userId = userId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
