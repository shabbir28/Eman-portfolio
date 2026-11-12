package model;

import java.sql.Date;

public class UserIssued {
    private int id; // UserIssuedID
    private int userId; // FK to UserReceived
    private Date issuedDate;
    private String remarks;
    // Assuming matching prompt: ID, UserID, IssuedDate, Remarks.

    public UserIssued() {}

    public UserIssued(int id, int userId, Date issuedDate, String remarks) {
        this.id = id;
        this.userId = userId;
        this.issuedDate = issuedDate;
        this.remarks = remarks;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Date getIssuedDate() { return issuedDate; }
    public void setIssuedDate(Date issuedDate) { this.issuedDate = issuedDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
