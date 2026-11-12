import java.sql.Connection;
public class MySQLConnection { public static Connection getConnection() { return DemoDatabase.connect(); } }