import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
    static Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");

    private static final String USER = dotenv.get("DB_USER");

    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection()
    {
        Connection connection = null;

        try
        {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection obtained");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println("Failed to connect to the database");
        }

        return connection;
    }
}
