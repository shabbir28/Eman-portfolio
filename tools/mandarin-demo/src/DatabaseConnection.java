import java.sql.Connection;
public class DatabaseConnection { public static Connection getConnection() { return DemoDatabase.connect(); } }