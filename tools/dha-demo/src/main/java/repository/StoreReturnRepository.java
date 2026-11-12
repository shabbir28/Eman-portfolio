package repository;
import database.ConnectionFactory;
import java.sql.*;
public class StoreReturnRepository {
 public int addReturn(int id,int q,Date d){return addReturnWithRemarks(id,q,d,"");}
 public int addReturnWithRemarks(int id,int q,Date d,String remarks){
  if(q<1)throw new IllegalArgumentException("Quantity must be positive");
  try(PreparedStatement p=ConnectionFactory.getInstance().getConnection().prepareStatement("INSERT INTO StoreReturn(StoreIssueID,Quantity,ReturnDate,Remarks) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
   p.setInt(1,id);p.setInt(2,q);p.setDate(3,d);p.setString(4,remarks);p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){r.next();return r.getInt(1);}
  }catch(SQLException e){throw new IllegalStateException("Could not return sample stock: "+e.getMessage(),e);}
 }
}