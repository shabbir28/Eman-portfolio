import java.sql.*;

/** Isolated, fictional demo data. No network or production database access. */
public final class DemoDatabase {
    private static Connection connection;
    public static synchronized Connection connect() {
        try {
            if (connection != null && !connection.isClosed()) return connection;
            connection = DriverManager.getConnection("jdbc:h2:mem:mandarin_demo;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
            try (Statement s = connection.createStatement()) {
                s.execute("CREATE TABLE login(username VARCHAR(80), password VARCHAR(80))");
                s.execute("INSERT INTO login VALUES('demo', 'demo123')");
                s.execute("CREATE TABLE Rooms(room_id INT AUTO_INCREMENT PRIMARY KEY, room_name VARCHAR(100) UNIQUE NOT NULL)");
                s.execute("INSERT INTO Rooms(room_name) VALUES('Demo Room 101'),('Demo Room 102'),('Demo Reception')");
                String[][] groups = {{"Furniture","Bed","Desk"},{"Electronics","Television","Lamp"},{"Miscellaneous","Wall clock","Waste basket"},{"Clean","Cleaning kit","Mop"},{"Laundry","Bath towel","Bed sheet"}};
                for (String[] g : groups) {
                    String t=g[0], id=t+"_id", name=t+"_name";
                    s.execute("CREATE TABLE "+t+"("+id+" INT AUTO_INCREMENT PRIMARY KEY,"+name+" VARCHAR(100) UNIQUE NOT NULL)");
                    s.execute("INSERT INTO "+t+"("+name+") VALUES('"+g[1]+"'),('"+g[2]+"')");
                    s.execute("CREATE TABLE "+t+"InRooms("+id+" INT REFERENCES "+t+"("+id+"), room_id INT REFERENCES Rooms(room_id), quantity INT NOT NULL CHECK(quantity >= 0))");
                    s.execute("INSERT INTO "+t+"InRooms VALUES(1,1,2),(2,2,3),(2,3,1)");
                }
                s.execute("CREATE TABLE KitchenItems(KitchenItems_id INT AUTO_INCREMENT PRIMARY KEY, KitchenItems_name VARCHAR(100) UNIQUE NOT NULL, quantity INT DEFAULT 0 CHECK(quantity >= 0))");
                s.execute("INSERT INTO KitchenItems(KitchenItems_name,quantity) VALUES('Dinner plate',24),('Tea cup',18),('Serving tray',6)");
            }
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize the sample inventory", e);
        }
    }
}
