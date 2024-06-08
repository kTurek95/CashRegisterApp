import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server
{
    private static final Connection connection = DatabaseConnection.getConnection();
    private static Map<String, String> productsPrice = new HashMap<>();
    static Dotenv dotenv = Dotenv.load();

    static int port = Integer.parseInt(dotenv.get("PORT"));

    public static Map<Integer, Integer> getProductsQuantity() {
        Map<Integer, Integer> productsIdAndQuantity = new HashMap<>();
        if (connection != null) {
            try {
                String query = "SELECT product_id, stock_quantity FROM Products";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    int productQuantity = resultSet.getInt("stock_quantity");
                    productsIdAndQuantity.put(productId, productQuantity);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return productsIdAndQuantity;
    }

    public static List<String> getProducts() {
        List<String> products = new ArrayList<>();
        if (connection != null) {
            try {
                String query = "SELECT product_name FROM Products";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    String product = resultSet.getString("product_name");
                    products.add(product);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return products;
    }

    public static Map<String, String> getProductsPrice() {
        if (connection != null) {
            try {
                String query = "SELECT product_name, price FROM Products";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    String product_name = resultSet.getString("product_name");
                    float product_price = resultSet.getFloat("price");
                    productsPrice.put(product_name, String.valueOf(product_price));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return productsPrice;
    }

    public static void updateProductQuantity(String productName, String productQuantity)
    {
        int intProductQuantity = Integer.parseInt(productQuantity);

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null)
            try
            {
                String selectQuery = "SELECT stock_quantity FROM Products WHERE product_name = ?";
                String updateQuery = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE product_name = ?";

                PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                selectStatement.setString(1, productName);
                ResultSet selectResultSet = selectStatement.executeQuery();

                if (selectResultSet.next())
                {
                    int currentQuantity = selectResultSet.getInt("stock_quantity");

                    if (currentQuantity >= intProductQuantity)
                    {
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setInt(1, intProductQuantity);
                        updateStatement.setString(2, productName);

                        int affectedRows = updateStatement.executeUpdate();
                        if (affectedRows > 0)
                        {
                            System.out.println("Quantity updated successfully.");
                        }
                        else
                        {
                            System.out.println("No rows affected.");
                        }
                    }
                    else
                    {
                        System.out.println("Not enough quantity available.");
                    }
                }
                else
                {
                    System.out.println("Product not found.");
                }

            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
    }


    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
}


static class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("New client handler started");

            out.println("PRODUCTS");
            List<String> products = Server.getProducts();
            for (String product : products) {
                out.println(product);
            }
            out.println("END_PRODUCTS");

            out.println("PRODUCTS_PRICE");
            Map<String, String> productsPrice = Server.getProductsPrice();
            for (String product : productsPrice.keySet()) {
                String price = productsPrice.get(product);
                out.println(product);
                out.println(price);
            }
            out.println("END_PRODUCTS_PRICE");

            out.println("PRODUCT_QUANTITY");
            Map<Integer, Integer> productsIdAndQuantity = Server.getProductsQuantity();
            for (int id : productsIdAndQuantity.keySet()) {
                int stock = productsIdAndQuantity.get(id);
                out.println(id);
                out.println(stock);
            }
            out.println("END_PRODUCTS_QUANTITY");

            try
            {
                String productNameFromClient;
                productNameFromClient = in.readLine();
                if (productNameFromClient != null) {
                    System.out.println("Product Name: " + productNameFromClient);
                } else {
                    System.out.println("Product Name is null");
                }

                String amountOfProductFromClient;
                amountOfProductFromClient = in.readLine();
                if (amountOfProductFromClient != null) {
                    System.out.println("Amount of product: " + amountOfProductFromClient);
                } else {
                    System.out.println("Amount of product is null");
                }

                if (productNameFromClient != null && amountOfProductFromClient != null) {
                    Server.updateProductQuantity(productNameFromClient, amountOfProductFromClient);
                    out.println("Quantity updated");
                } else {
                    out.println("Failed to update quantity");
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }


        } catch (IOException e)
        {
            System.err.println("IOException in ClientHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
}
