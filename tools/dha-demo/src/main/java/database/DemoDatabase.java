package database;
import java.sql.*;
import java.nio.charset.StandardCharsets;
public final class DemoDatabase {
 public static void initialize(Connection c)throws Exception{
  String schema=new String(DemoDatabase.class.getResourceAsStream("/demo-schema.sql").readAllBytes(),StandardCharsets.UTF_8);
  try(Statement s=c.createStatement()){
   for(String sql:schema.split(";"))if(!sql.isBlank())s.execute(sql);
   s.execute("ALTER TABLE StoreReturn ADD Remarks VARCHAR(500)");
   for(String table:new String[]{"StoreReceived","StoreIssued","StoreReturn","AuctionReceived","AuctionIssued","UserReceived","UserIssued"})s.execute("CREATE TRIGGER demo_"+table+" AFTER INSERT, UPDATE, DELETE ON "+table+" FOR EACH ROW CALL 'database.DemoStockTrigger'");
   s.execute("INSERT INTO Users(username,password,role) VALUES('demo','demo123','ADMIN')");
   s.execute("INSERT INTO Branch(BranchName) VALUES('Demo Administration'),('Demo IT'),('Demo Operations')");
   s.execute("INSERT INTO DHAEmployee(DHAID,EmpName,BranchID,Designation,Scale) VALUES('DEMO-001','Demo Employee A',1,'Office Assistant',14),('DEMO-002','Demo Employee B',2,'IT Officer',16),('DEMO-003','Demo Employee C',3,'Store Coordinator',14)");
   s.execute("INSERT INTO ReceivedFrom(TypeName) VALUES('Minute Sheet'),('Petty Cash'),('Replacement'),('UserReceived')");
   s.execute("INSERT INTO IssueType(TypeName) VALUES('Temporary'),('Permanent')");
   s.execute("INSERT INTO UserStatus(StatusName) VALUES('Issued'),('Pending')");
   s.execute("INSERT INTO StoreReceived(ItemName,Quantity,ReceivedFromID,ReceiveDate,UnitPrice,Remarks) VALUES('Office chair',24,1,'2026-11-01',8500,'Fictional sample stock'),('A4 paper ream',80,2,'2026-11-02',1200,'Fictional sample stock'),('Wireless mouse',30,1,'2026-11-03',1800,'Fictional sample stock'),('Printer toner',3,2,'2026-11-04',6500,'Fictional sample stock'),('LED monitor',12,1,'2026-11-05',28000,'Fictional sample stock'),('Extension board',18,3,'2026-11-06',1500,'Fictional sample stock')");
   s.execute("INSERT INTO MinuteSheet(PurchaseOrderNo,Vendor,StoreReceivedID) VALUES('DEMO-PO-001','Sample Supplier',1),('DEMO-PO-002','Sample Supplier',3),('DEMO-PO-003','Sample Supplier',5)");
   s.execute("INSERT INTO PettyCash(VoucherNo,StoreReceivedID) VALUES('DEMO-PC-001',2),('DEMO-PC-002',4)");
   s.execute("INSERT INTO Replacement(BranchID,StoreReceivedID) VALUES(3,6)");
   s.execute("INSERT INTO StoreIssued(StoreInventoryID,Quantity,DHAID,IssuedDate,IssueTypeID,RegisterNo,Remarks) VALUES(1,4,'DEMO-001','2026-11-07',1,'DEMO-R-001','Training room'),(3,6,'DEMO-002','2026-11-08',2,'DEMO-R-002','Sample allocation'),(5,2,'DEMO-002','2026-11-09',1,'DEMO-R-003','Sample allocation')");
   s.execute("INSERT INTO LowStock(ItemName,LowStockLimit) VALUES('Printer toner',5),('Office chair',10),('Wireless mouse',10)");
   s.execute("INSERT INTO AuctionReceived(ItemName,Quantity,LetterNo,ReceiveDate,Remarks) VALUES('Retired desk',8,'DEMO-A-001','2026-11-01','Fictional auction stock'),('Retired printer',3,'DEMO-A-002','2026-11-02','Fictional auction stock')");
   s.execute("INSERT INTO AuctionIssued(AuctionID,Quantity,IssuedDate,Remarks) VALUES(1,2,'2026-11-09','Sample disposal')");
   s.execute("INSERT INTO UserReceived(ItemName,Quantity,PurchaseOrderNo,Vendor,ReceiveDate,DHAID,BranchID,WarrantyDemand,WarrantyProvided,StatusID,Reason,Remarks) VALUES('Demo laptop',2,'DEMO-U-001','Sample Supplier','2026-11-05','DEMO-002',2,'12 months','12 months',2,'Awaiting allocation','Fictional sample'),('Desk phone',1,'DEMO-U-002','Sample Supplier','2026-11-06','DEMO-001',1,'12 months','12 months',1,'Office use','Fictional sample')");
   s.execute("INSERT INTO UserIssued(UserID,IssuedDate,Remarks) VALUES(2,'2026-11-07','Sample allocation')");
  }
 }
}