package database;
import java.sql.*;
import org.h2.api.Trigger;
/** Derive balances from receipt, issue and return records. */
public final class DemoStockTrigger implements Trigger {
 public void fire(Connection c,Object[] before,Object[] after)throws SQLException{
  try(Statement s=c.createStatement()){
   for(String kind:new String[]{"Store","Auction"}){
    s.executeUpdate("INSERT INTO "+kind+"Inventory(ItemName,Quantity) SELECT r.ItemName,0 FROM "+kind+"Received r WHERE NOT EXISTS(SELECT 1 FROM "+kind+"Inventory i WHERE i.ItemName=r.ItemName) GROUP BY r.ItemName");
    s.executeUpdate("UPDATE "+kind+"Inventory i SET Quantity=(SELECT COALESCE(SUM(r.Quantity),0) FROM "+kind+"Received r WHERE r.ItemName=i.ItemName)");
    s.executeUpdate("INSERT INTO "+kind+"Remaining(ItemName,Quantity) SELECT i.ItemName,0 FROM "+kind+"Inventory i WHERE NOT EXISTS(SELECT 1 FROM "+kind+"Remaining r WHERE r.ItemName=i.ItemName)");
    String key=kind.equals("Store")?"StoreInventoryID":"AuctionID";
    String returns=kind.equals("Store")?"+COALESCE((SELECT SUM(t.Quantity) FROM StoreReturn t JOIN StoreIssued x ON x.ID=t.StoreIssueID JOIN StoreInventory i ON i.ID=x.StoreInventoryID WHERE i.ItemName=r.ItemName),0)":"";
    s.executeUpdate("UPDATE "+kind+"Remaining r SET Quantity=(SELECT i.Quantity FROM "+kind+"Inventory i WHERE i.ItemName=r.ItemName)-COALESCE((SELECT SUM(x.Quantity) FROM "+kind+"Issued x JOIN "+kind+"Inventory i ON i.ID=x."+key+" WHERE i.ItemName=r.ItemName),0)"+returns);
    try(ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM "+kind+"Remaining WHERE Quantity<0")){rs.next();if(rs.getInt(1)>0)throw new SQLException("Insufficient sample stock");}
   }
   try(ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM StoreIssued i WHERE COALESCE((SELECT SUM(r.Quantity) FROM StoreReturn r WHERE r.StoreIssueID=i.ID),0)>i.Quantity")){rs.next();if(rs.getInt(1)>0)throw new SQLException("Return exceeds outstanding allocation");}
   s.executeUpdate("DELETE FROM Pending");
   s.executeUpdate("INSERT INTO Pending(UserID,Quantity) SELECT ID,Quantity FROM UserReceived u WHERE StatusID=2 AND NOT EXISTS(SELECT 1 FROM UserIssued i WHERE i.UserID=u.ID)");
  }
 }
}