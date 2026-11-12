import java.sql.*;
public class DemoSmokeTest {
 public static void main(String[] args) throws Exception {
  Connection c=DemoDatabase.connect();
  try(Statement s=c.createStatement()) {
   try(ResultSet r=s.executeQuery("SELECT * FROM login WHERE username='demo' AND password='demo123'")){if(!r.next())throw new AssertionError("Login");}
   for(String t:new String[]{"Furniture","Electronics","Miscellaneous","Clean","Laundry"}) {
    String id=t+"_id", name=t+"_name";
    s.executeUpdate("INSERT INTO "+t+"("+name+") VALUES('Test item')");
    s.executeUpdate("INSERT INTO "+t+"InRooms VALUES((SELECT "+id+" FROM "+t+" WHERE "+name+"='Test item'),1,5)");
    s.executeUpdate("UPDATE "+t+"InRooms SET quantity=3 WHERE "+id+"=(SELECT "+id+" FROM "+t+" WHERE "+name+"='Test item')");
    try(ResultSet r=s.executeQuery("SELECT "+t+"."+name+", Rooms.room_name, "+t+"InRooms.quantity FROM "+t+"InRooms JOIN "+t+" ON "+t+"InRooms."+id+"="+t+"."+id+" JOIN Rooms ON "+t+"InRooms.room_id=Rooms.room_id WHERE "+t+"."+name+"='Test item'")) {if(!r.next()||r.getInt(3)!=3)throw new AssertionError(t);}
    s.executeUpdate("DELETE FROM "+t+"InRooms WHERE "+id+"=(SELECT "+id+" FROM "+t+" WHERE "+name+"='Test item')");
   }
   s.executeUpdate("INSERT INTO KitchenItems(KitchenItems_name) VALUES('Test cup')");
   s.executeUpdate("UPDATE KitchenItems SET quantity=8 WHERE KitchenItems_name='Test cup'");
   try(ResultSet r=s.executeQuery("SELECT quantity FROM KitchenItems WHERE KitchenItems_name='Test cup'")){if(!r.next()||r.getInt(1)!=8)throw new AssertionError("Kitchen");}
  }
  System.out.println("PASS: demo login, all five room inventory categories, add/update/search/remove, kitchen inventory");
 }
}
