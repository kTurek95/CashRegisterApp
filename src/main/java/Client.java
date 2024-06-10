import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

    static Dotenv dotenv = Dotenv.load();

    private static String serverAddress = dotenv.get("LOCALHOST");
    private static int port = Integer.parseInt(dotenv.get("PORT"));

    public List<String> fetchProducts() {
        List<String> products = new ArrayList<>();

        try
        {
            Socket socket = new Socket(serverAddress, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server");

            String product;
            while ((product = reader.readLine()) != null) {
                if (product.equals("PRODUCTS")) {
                    while (!(product = reader.readLine()).equals("END_PRODUCTS")) {
                        products.add(product);
                    }
                    break;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return products;
    }

    public Map<String, String> fetchProductNameAndPrice() {
        Map<String, String> productsPrice = new HashMap<>();

        try {
            Socket socket = new Socket(serverAddress, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server");

            String line;
            String product = null;

            while ((line = reader.readLine()) != null) {
                if (line.equals("PRODUCTS_PRICE")) {
                    while (!(line = reader.readLine()).equals("END_PRODUCTS_PRICE")) {
                        if (product == null) {
                            product = line;
                        } else {
                            productsPrice.put(product, line);
                            product = null;
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return productsPrice;
    }

    public String sendValuesToServer(String product, String amount)
    {
        String message = null;
        try
        {
            Socket socket = new Socket(serverAddress, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to the server");

            System.out.println("Sending product name: " + product);
            out.println(product);

            System.out.println("Sending product quantity: " + amount);
            out.println(amount);

            String response;
            while ((response = reader.readLine()) != null)
            {
                if ("TRANSACTION MESSAGE".equals(response))
                {
                    message = reader.readLine();
                    System.out.println("Transaction message: " + message);
                    break;
                }
            }

        } catch (IOException e)
        {
            System.err.println("IOException in Client: " + e.getMessage());
            e.printStackTrace();
        }
        return message;
    }

}
