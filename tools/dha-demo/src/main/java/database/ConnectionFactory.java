package database;
import java.sql.*;
/** Offline demo: no production or LAN connection. */
public final class ConnectionFactory {
 private static final ConnectionFactory INSTANCE=new ConnectionFactory();
 private final Connection connection;
 private ConnectionFactory(){try{connection=DriverManager.getConnection("jdbc:h2:mem:dha_demo;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1","sa","");DemoDatabase.initialize(connection);}catch(Exception e){throw new IllegalStateException("Cannot initialize sample inventory",e);}}
 public static ConnectionFactory getInstance(){return INSTANCE;}
 public Connection getConnection(){return connection;}
 public Connection createLanConnection(){return null;}
}