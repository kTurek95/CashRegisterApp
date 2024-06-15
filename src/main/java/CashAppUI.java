import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CashAppUI extends JFrame
{
    JDialog removeItemDialog;
    ButtonGroup buttonGroup;
    String itemToRemove;
    String amountToRemove;
    double amountToSubtract;
    double selectedProductPrice;
    int selectedProductAmount;
    private static JRadioButton radioButton;
    String searchedProduct;
    private static int num2Int;
    private static String filePath;
    static Client client = new Client();
    private static Map<String, String> productsPrice = new HashMap<>();
    private static double transactionSum;
    Map<String, String> itemsInBasket = new HashMap<>();
    private static double num1 = 0;
    private static String operator = "";
    private static double result = 0;
    private JComboBox<String> productComboBox;
    static String buttonNumbers = "";
    private static JButton[] buttons;
    private static JTextField findProductField;
    private static JButton shoppingCard, totalButton, cashButton, cardButton, cancelButton, printReceiptButton, searchButton, removeItemButton, confirmButton;
    private static JTextArea displayArea;
    private static JPanel middlePanel;
    static String selectedProduct;
    private static JLabel productLabel;
    private static JPanel topPanel;
    private static JPanel bottomPanel;
    private static JPanel leftPanel;
    private static JPanel rightPanel;

    String[] buttonLabels = {"7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "+/-"};
    public CashAppUI()
    {
        setTitle("CashRegisterApp");
        setSize(800,350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        getItemsPrice();
        addButtonsListeners();
        addComponentsToFrame();
        addElementsToComboBox();
    }

    public void initComponents()
    {
        topPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel = new JPanel(new GridLayout(2, 3));
        leftPanel = new JPanel(new GridLayout(2, 1));
        rightPanel = new JPanel(new GridLayout(5, 4));
        middlePanel = new JPanel(new GridLayout(1, 1));

        productLabel = new JLabel("Choose Product:");

        findProductField = new JTextField();

        displayArea = new JTextArea();
        displayArea.setEditable(false);

        productComboBox = new JComboBox<>();
        productComboBox.addItem("");

        shoppingCard = new JButton("Shopping card");
        totalButton = new JButton("Total");
        cashButton = new JButton("Pay by Cash");
        cardButton = new JButton("Pay by Card");
        cancelButton = new JButton("Cancel");
        printReceiptButton = new JButton();
        searchButton = new JButton("Search");
        removeItemButton = new JButton("Remove Item");


        buttons = new JButton[buttonLabels.length];
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            buttons[i] = button;
        }
    }

    public void addButtonsListeners()
    {
        productComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedProduct = (String) productComboBox.getSelectedItem();
                for (String product: productsPrice.keySet())
                {
                    if (Objects.equals(selectedProduct, product))
                    {
                        String price = productsPrice.get(product);
                        displayArea.setText(String.valueOf(price));

                    }
                }
            }
        });

        shoppingCard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCardWindow();
            }
        });

        totalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "The total for the transaction is: " + transactionSum);
            }
        });

         cashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardAndCashButtonListener();
            }
        });

        cardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardAndCashButtonListener();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayArea.setText("");
                transactionSum = 0;
                itemsInBasket.clear();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchedProduct = findProductField.getText();
                for (String product: productsPrice.keySet())
                {
                    String price = productsPrice.get(product);
                    if (searchedProduct.equals(product) && productsPrice.containsKey(searchedProduct))
                    {
                        displayArea.setText("\n Price for  " + product + " is " + price + "\n");
                        findProductField.setText("");
                        break;
                    }
                    else
                    {
                         displayArea.setText("\n There is no such product.");
                    }

                }
            }
        });

        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemsInBasket.isEmpty())
                {
                    JOptionPane.showMessageDialog(null, "Basket is empty");
                }
                else
                {
                    removeItemDialog = new JDialog();
                    removeItemDialog.setLayout(new BorderLayout());
                    removeItemDialog.setSize(200, 200);
                    removeItemDialog.setLocationRelativeTo(null);
                    removeItemDialog.setVisible(true);

                    buttonGroup = new ButtonGroup();
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    removeItemDialog.add(panel,BorderLayout.CENTER);

                    confirmButton = new JButton("Confirm");
                    removeItemDialog.add(confirmButton, BorderLayout.SOUTH);

                    for (String key: itemsInBasket.keySet())
                    {
                        radioButton = new JRadioButton(key);
                        buttonGroup.add(radioButton);
                        panel.add(radioButton);
                    }
                }

                if (confirmButton != null)
                {
                    confirmButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            AbstractButton selectedButton = null;

                            for (AbstractButton button : Collections.list(buttonGroup.getElements()))
                            {
                                if (button.isSelected())
                                {
                                    selectedButton = button;
                                    break;
                                }
                            }
                            if (selectedButton != null)
                            {
                                String selectedProductToRemove = selectedButton.getText();
                                System.out.println("Selected product to remove " + selectedProductToRemove);

                                for (String product: productsPrice.keySet())
                                {
                                    if (selectedProductToRemove.equals(product))
                                    {
                                        selectedProductPrice = (Double.valueOf(productsPrice.get(product)));
                                    }

                                }

                                for (String key: itemsInBasket.keySet())
                                {
                                    String value = itemsInBasket.get(key);
                                    if (selectedProductToRemove.equals(key))
                                    {
                                        itemToRemove = key;
                                        amountToRemove = value;
                                        selectedProductAmount = Integer.parseInt(value);
                                        removeItemDialog.dispose();

                                    }
                                }

                                itemsInBasket.remove(itemToRemove, amountToRemove);
                                amountToSubtract = selectedProductAmount * selectedProductPrice;
                                transactionSum -= amountToSubtract;
                                if (itemsInBasket.isEmpty())
                                {
                                    transactionSum = 0;
                                }
                            }
                        }
                    });

                }
            }
        });
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String digit = buttons[index].getText();
                    if (digit.matches("[0-9]")) {
                        buttonNumbers += digit;
                        displayArea.setText(buttonNumbers);
                    } else if (digit.equals("+") || digit.equals("-") || digit.equals("*") || digit.equals("/")) {
                        num1 = Double.parseDouble(displayArea.getText());
                        operator = digit;
                        displayArea.setText("");
                        buttonNumbers = "";
                    } else if (digit.equals("C")) {

                        result = 0;
                        displayArea.setText("");
                        buttonNumbers = "";
                    } else if (digit.equals("+/-")) {
                        double number = Double.parseDouble(displayArea.getText());
                        double negatedNumber = number * -1;
                        displayArea.setText(String.valueOf(negatedNumber));
                    } else if (digit.equals("=")) {
                        double num2 = Double.parseDouble(displayArea.getText());
                        switch (operator) {
                            case "+":
                                result = num1 + num2;
                                break;
                            case "-":
                                result = num1 - num2;
                                break;
                            case "*":
                                result = num1 * num2;
                                break;
                            case "/":
                                if (num2 != 0) {
                                    result = num1 / num2;
                                } else {
                                    displayArea.setText("Error");
                                    return;
                                }
                                break;
                        }
                        num2Int = (int) Math.round(num2);
                        displayArea.setText(String.valueOf(result));
                        transactionSum += result;
                        itemsInBasket.put(selectedProduct, String.valueOf(num2Int));
                    }
                }
            });
        }
    }

    public void addComponentsToFrame()
    {
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(middlePanel, BorderLayout.CENTER);

        leftPanel.add(productLabel);
        leftPanel.add(productComboBox);

        middlePanel.add(displayArea);

        topPanel.add(findProductField);
        topPanel.add(searchButton);
        topPanel.add(shoppingCard);

        for (JButton button: buttons)
        {
            rightPanel.add(button);
        }

        bottomPanel.add(totalButton);
        bottomPanel.add(cashButton);
        bottomPanel.add(cardButton);
        bottomPanel.add(cancelButton);
        bottomPanel.add(printReceiptButton);
        bottomPanel.add(removeItemButton);


    }

    public void addElementsToComboBox()
    {
        List<String> products = client.fetchProducts();

        for(String product: products)
        {
            productComboBox.addItem(product);
        }
    }

    public void getItemsPrice()
    {
        productsPrice = client.fetchProductNameAndPrice();
    }

    public void openCardWindow()
    {
        JDialog cardWindow = new JDialog(this, "Shopping card");
        cardWindow.setSize(200, 200);
        cardWindow.setLocationRelativeTo(this);
        cardWindow.setVisible(true);

        JTextArea textarea = new JTextArea();
        textarea.setEditable(false);
        cardWindow.add(textarea);

        for(String key: itemsInBasket.keySet())
        {
            String value = itemsInBasket.get(key);
            textarea.append(key + " quantity:  " + value + "\n");
        }


    }

    public void cardAndCashButtonListener()
    {
        if (itemsInBasket.isEmpty())
        {
            JOptionPane.showMessageDialog(null,"Basket is empty");
        }
        else
        {
            JDialog dialog = new JDialog();
            dialog.setLayout(new BorderLayout());
            dialog.setSize(150, 150);
            dialog.setLocationRelativeTo(null);
            JTextArea area = new JTextArea();
            JButton zaplac = new JButton("Pay");
            area.setEditable(false);
            dialog.add(area, BorderLayout.CENTER);
            dialog.add(zaplac, BorderLayout.SOUTH);
            area.append("Amount to pay" + transactionSum);

            zaplac.addActionListener(g ->
            {
                for (String product : itemsInBasket.keySet())
                {
                    String amount = itemsInBasket.get(product);
                    System.out.println("Product aame: " + product + " amount: " + amount);
                    String message = client.sendValuesToServer(product, amount);
                    if (message.equals("Quantity updated successfully."))
                    {
                        dialog.dispose();
                        displayArea.setText("");
                        JDialog dialog1 = new JDialog();
                        dialog1.setLayout(new BorderLayout());
                        dialog1.setSize(150, 80);
                        dialog1.setLocationRelativeTo(null);
                        dialog1.setVisible(true);

                        JLabel receiptQuestionLabel = new JLabel("Do you want receipt?");
                        JButton yesButton = new JButton("Yes");
                        JButton noButton = new JButton("No");

                        yesButton.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                dialog1.dispose();
                                filePath = "src/main/java/receipt.txt";
                                File file = new File(filePath);
                                if (file.exists())
                                {
                                    try {
                                        if (!itemsInBasket.isEmpty())
                                        {
                                            FileWriter fileWriter = new FileWriter(filePath);
                                            for (String key: itemsInBasket.keySet())
                                            {
                                                String value = itemsInBasket.get(key);
                                                fileWriter.write(key + " quantity: " + value + "\n");

                                            }
                                            fileWriter.write("Transaction value: " + transactionSum);
                                            fileWriter.close();
                                            JOptionPane.showMessageDialog(null, "The bill has been generated");
                                            itemsInBasket.clear();
                                            transactionSum = 0;
                                        }
                                        else
                                        {
                                            JOptionPane.showMessageDialog(null, "Basket is empty");
                                        }
                                    }
                                    catch (IOException ex)
                                    {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }
                        });


                        noButton.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                dialog1.dispose();
                                transactionSum = 0;
                                itemsInBasket.clear();
                                JOptionPane.showMessageDialog(null, "Transaction completed");
                            }
                        });


                        dialog1.add(receiptQuestionLabel, BorderLayout.NORTH);
                        dialog1.add(yesButton, BorderLayout.WEST);
                        dialog1.add(noButton, BorderLayout.EAST);
                    }
                    else if (message.equals("Not enough quantity available."))
                    {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(null, "We don't have enough quantity in stock");
                        transactionSum = 0;
                        itemsInBasket.clear();
                        break;
                    }
                }

            });

            dialog.setVisible(true);
        }
        }

}
