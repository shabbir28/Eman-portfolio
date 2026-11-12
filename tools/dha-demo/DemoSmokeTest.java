import database.ConnectionFactory;
import repository.*;
import java.sql.*;
public class DemoSmokeTest {
 static Connection c=ConnectionFactory.getInstance().getConnection();
 static int scalar(String sql)throws Exception{try(var s=c.createStatement();var r=s.executeQuery(sql)){r.next();return r.getInt(1);}}
 static void check(boolean ok,String m){if(!ok)throw new AssertionError(m);}
 public static void main(String[] args)throws Exception{
  check(new UserRepository().findByName("demo").getPassword().equals("demo123"),"login");
  check(scalar("SELECT Quantity FROM StoreRemaining WHERE ItemName='Office chair'")==20,"seed balance");
  var receipts=new ReceivedRepository();var issues=new IssueRepository();var returns=new StoreReturnRepository();
  receipts.addStoreReceived("Office chair",5,2,Date.valueOf("2026-11-11"),8500,"Smoke test");
  check(scalar("SELECT Quantity FROM StoreRemaining WHERE ItemName='Office chair'")==25,"receive");
  int id=issues.addStoreIssued(1,2,"DEMO-001",Date.valueOf("2026-11-11"),1,"TEST","test");
  check(scalar("SELECT Quantity FROM StoreRemaining WHERE ItemName='Office chair'")==23,"issue exactly once");
  returns.addReturnWithRemarks(id,1,Date.valueOf("2026-11-11"),"test");
  check(scalar("SELECT Quantity FROM StoreRemaining WHERE ItemName='Office chair'")==24,"return exactly once");
  boolean blocked=false;try{returns.addReturn(id,2,Date.valueOf("2026-11-11"));}catch(RuntimeException e){blocked=true;}
  check(blocked,"over-return rejected");check(scalar("SELECT Quantity FROM StoreRemaining WHERE ItemName='Office chair'")==24,"rollback");
  blocked=false;try{issues.addStoreIssued(1,9999,"DEMO-001",Date.valueOf("2026-11-11"),1,"TEST","invalid");}catch(RuntimeException e){blocked=true;}
  check(blocked,"over-issue rejected");check(scalar("SELECT Quantity FROM StoreRemaining WHERE ItemName='Office chair'")==24,"issue rollback");
  receipts.addAuctionReceived("Retired desk",2,"TEST",Date.valueOf("2026-11-11"),"test");issues.addAuctionIssued(1,1,Date.valueOf("2026-11-11"),"test");
  check(scalar("SELECT Quantity FROM AuctionRemaining WHERE ItemName='Retired desk'")==7,"auction balance");
  issues.addUserIssued(1,Date.valueOf("2026-11-11"),"test");check(scalar("SELECT COUNT(*) FROM Pending")==0,"user pending cleared");
  check(new StoreInventoryRepository().getInventoryOverview().size()==6,"inventory query");
  check(new InventoryRepository().fetchStoreIssuedQuantityByStatus("Temporary")==7,"dashboard subtracts returns");
  new InventoryRepository().fetchLowStockItems(20);new InventoryRepository().fetchUserStats();new InventoryRepository().fetchAuctionStats();
  System.out.println("PASS: login, receipt, issue, partial return, over-return/over-issue rollback, auction, user allocation, inventory and dashboard queries");
 }
}