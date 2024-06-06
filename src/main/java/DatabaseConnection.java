import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
    private static final String URL = "";

    private static final String USER = "";

    private static final String PASSWORD = "";

    public static Connection getConnection()
    {
        Connection connection = null;

        try
        {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Połączenie uzyskane");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println("Nie udało się połączyć z bazą danych");
        }

        return connection;
    }
}
