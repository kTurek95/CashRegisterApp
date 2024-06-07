import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CashAppUI cashAppUI = new CashAppUI();
                cashAppUI.setVisible(true);
            }
        });

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}