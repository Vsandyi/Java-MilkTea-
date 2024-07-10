/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cofeeshop;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author franc
 */
public class mainFormController implements Initializable {

    @FXML
    private Label dashboard_NC;

    @FXML
    private Label dashboard_NSP;

    @FXML
    private Label dashboard_TI;

    @FXML
    private Label dashboard_TotalI;

    @FXML
    private TableColumn<customersData, String> customers_col_cashier;

    @FXML
    private TableColumn<customersData, String> customers_col_customerID;

    @FXML
    private TableColumn<customersData, String> customers_col_date;

    @FXML
    private TableColumn<customersData, String> customers_col_total;

    @FXML
    private AnchorPane customers_form;

    @FXML
    private TableView<customersData> customers_tableView;
    @FXML
    private Button inventory_btn;
    @FXML
    private Button menu_btn;
    @FXML
    private Button customers_btn;
    @FXML
    private Button dashboard_btn;
    @FXML
    private Label username;
    @FXML
    private AnchorPane inventory_form;
    @FXML
    private TableView<productData> inventory_tableView;
    @FXML
    private TableColumn<productData, String> inventory_col_productID;
    @FXML
    private TableColumn<productData, String> inventory_col_productName;
    @FXML
    private TableColumn<productData, String> inventory_col_type;
    @FXML
    private TableColumn<productData, String> inventory_col_stock;
    @FXML
    private TableColumn<productData, String> inventory_col_price;
    @FXML
    private TableColumn<productData, String> inventory_col_status;
    @FXML
    private TableColumn<productData, String> inventory_col_date;
    @FXML
    private ImageView inventory_imageView;
    @FXML
    private Button inventory_importBtn;
    @FXML
    private Button inventory_otherBtn;
    @FXML
    private Button inventory_addBtn;
    @FXML
    private Button inventory_updateBtn;
    @FXML
    private Button inventory_clearBtn;
    @FXML
    private Button inventory_deleteBtn;
    @FXML
    private Button logout_btn;
    @FXML
    private BorderPane main_form;
    @FXML
    private TextField inventory_productID;
    @FXML
    private TextField inventory_productName;
    @FXML
    private TextField inventory_stock;
    @FXML
    private TextField inventory_price;
    @FXML
    private ComboBox<?> inventory_status;
    @FXML
    private ComboBox<?> inventory_type;

    @FXML
    private TextField menu_amount;
    @FXML
    private Label menu_change;
    @FXML
    private TableColumn<productData, String> menu_col_price;
    @FXML
    private TableColumn<productData, String> menu_col_productName;
    @FXML
    private TableColumn<productData, String> menu_col_quantity;
    @FXML
    private AnchorPane menu_form;
    @FXML
    private AnchorPane dashboard_form;
    @FXML
    private GridPane menu_gridPane;
    @FXML
    private Button menu_payBtn;
    @FXML
    private Button menu_removeBtn;
    @FXML
    private ScrollPane menu_scrollPane;
    @FXML
    private TableView<productData> menu_tableView;
    @FXML
    private Label menu_total;

    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private Alert alert;
    private Image image;

    public void handleShowButtons() {
        // Show the hidden buttons
        inventory_updateBtn.setVisible(true);
        inventory_clearBtn.setVisible(true);
        inventory_deleteBtn.setVisible(true);

        // Disable the main button after showing the other buttons
        inventory_otherBtn.setVisible(false);
    }

    public void hideButtons() {
        inventory_updateBtn.setVisible(false);
        inventory_clearBtn.setVisible(false);
        inventory_deleteBtn.setVisible(false);
    }

    public void showTotalCustomers() {
        String sqlDay = "SELECT COUNT(id) FROM receipt WHERE date = ?";
        String sqlWeek = "SELECT COUNT(id) FROM receipt WHERE date >= ? AND date <= ?";
        String sqlMonth = "SELECT COUNT(id) FROM receipt WHERE YEAR(date) = YEAR(?) AND MONTH(date) = MONTH(?)";
        String sqlDaysInMonth = "SELECT COUNT(DISTINCT date) FROM receipt WHERE YEAR(date) = YEAR(?) AND MONTH(date) = MONTH(?)";

        connect = database.connectDB();

        try {
            int ncDay = 0;
            int ncWeek = 0;
            int ncMonth = 0;
            int daysInMonth = 0;
            float avgCustomersPerDay = 0;

            // Current date
            Date currentDate = new Date();
            java.sql.Date sqlCurrentDate = new java.sql.Date(currentDate.getTime());

            // For day
            prepare = connect.prepareStatement(sqlDay);
            prepare.setDate(1, sqlCurrentDate);
            result = prepare.executeQuery();
            if (result.next()) {
                ncDay = result.getInt(1);
            }

            // For week
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            java.sql.Date weekStartDate = new java.sql.Date(calendar.getTimeInMillis());
            calendar.add(Calendar.DATE, 6); // Move to Saturday
            java.sql.Date weekEndDate = new java.sql.Date(calendar.getTimeInMillis());
            prepare = connect.prepareStatement(sqlWeek);
            prepare.setDate(1, weekStartDate);
            prepare.setDate(2, weekEndDate);
            result = prepare.executeQuery();
            if (result.next()) {
                ncWeek = result.getInt(1);
            }

            // For month
            calendar.setTime(currentDate);
            java.sql.Date monthDate = new java.sql.Date(calendar.getTimeInMillis());
            prepare = connect.prepareStatement(sqlMonth);
            prepare.setDate(1, monthDate);
            prepare.setDate(2, monthDate);
            result = prepare.executeQuery();
            if (result.next()) {
                ncMonth = result.getInt(1);
            }

            // Number of days with at least one customer in the current month
            prepare = connect.prepareStatement(sqlDaysInMonth);
            prepare.setDate(1, monthDate);
            prepare.setDate(2, monthDate);
            result = prepare.executeQuery();
            if (result.next()) {
                daysInMonth = result.getInt(1);
            }

            // Calculate average customers per day for the current month
            if (daysInMonth > 0) {
                avgCustomersPerDay = (float) ncMonth / daysInMonth;
            }

            // Display number of customers and average customers per day
            String message = "Number of Customers:\n";
            message += "Today: " + ncDay + "\n";
            message += "Weekly: " + ncWeek + "\n";
            message += "Monthly: " + ncMonth + "\n";
            message += "Average Customers per Day: " + avgCustomersPerDay + "\n";

            JOptionPane.showMessageDialog(null, message, "Number of Customers", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dashboardDisplayNC() {

        String sql = "SELECT COUNT(id) FROM receipt";
        connect = database.connectDB();

        try {
            int nc = 0;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            if (result.next()) {
                nc = result.getInt("COUNT(id)");
            }
            dashboard_NC.setText(String.valueOf(nc));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dashboardDisplayTI() {
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String sql = "SELECT SUM(total) FROM receipt WHERE date = '"
                + sqlDate + "'";

        connect = database.connectDB();

        try {
            double ti = 0;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            if (result.next()) {
                ti = result.getDouble("SUM(total)");
            }

            dashboard_TI.setText("₱" + ti);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayTodaysProductsSold() {
        // Get today's date
        LocalDate today = LocalDate.now();
        System.out.println("Today's Date: " + today); // Debug print to verify the date

        // SQL query to get product names, total quantities, and total prices sold today
        String sql = "SELECT prod_name, SUM(quantity) AS total_quantity, SUM(price) AS total_price "
                + "FROM customer WHERE date = ? GROUP BY prod_name ORDER BY total_price DESC  ";

        // Initialize variables for database connection, prepared statement, and result set
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            // Establish the database connection
            connect = database.connectDB();

            // Prepare the SQL statement
            prepare = connect.prepareStatement(sql);
            prepare.setDate(1, java.sql.Date.valueOf(today));

            // Execute the query
            result = prepare.executeQuery();

            // Check if the result set is empty
            if (!result.isBeforeFirst()) {
                System.out.println("No products sold today.");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Today's Products Sold");
                alert.setHeaderText(null);
                alert.setContentText("No products sold today.");
                alert.showAndWait();
                return;
            }

            // Create a GridPane to display the results in a tabular format
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // Add column headers
            grid.addRow(0, new Label("Product Name"), new Label("Total Quantity"), new Label("Total Price"));

            int row = 1;

            // Iterate through the result set and add each product, total quantity, and total price to the GridPane
            while (result.next()) {
                String productName = result.getString("prod_name");
                int totalQuantity = result.getInt("total_quantity");
                double totalPrice = result.getDouble("total_price");

                grid.addRow(row++, new Label(productName), new Label(Integer.toString(totalQuantity)), new Label("₱" + Double.toString(totalPrice)));
            }

            // Create an alert with the GridPane as its content
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Today's Products Sold");
            alert.setHeaderText(null);
            alert.getDialogPane().setContent(grid);

            // Show the alert
            alert.showAndWait();

        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
        } finally {
            // Close the ResultSet, PreparedStatement, and Connection objects to release resources
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dashboardTotalI() {
        String sql = "SELECT SUM(total) FROM receipt";

        connect = database.connectDB();

        try {
            float ti = 0;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            if (result.next()) {
                ti = result.getFloat("SUM(total)");
            }
            dashboard_TotalI.setText("₱" + ti);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dashboardNSP() {

        String sql = "SELECT SUM(quantity) FROM customer";

        connect = database.connectDB();

        try {
            int q = 0;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            if (result.next()) {
                q = result.getInt("SUM(quantity)");
            }
            dashboard_NSP.setText(String.valueOf(q));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayProductsSold() {
        // SQL query to get product names and quantities sold, sorted by total_quantity in descending order
        String sql = "SELECT prod_name, SUM(quantity) AS total_quantity FROM customer GROUP BY prod_name ORDER BY total_quantity DESC";

        // Initialize variables for database connection, prepared statement, and result set
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            // Establish the database connection
            connect = database.connectDB();

            // Prepare the SQL statement
            prepare = connect.prepareStatement(sql);

            // Execute the query
            result = prepare.executeQuery();

            // Create a GridPane to display the results in a tabular format
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // Add column headers
            grid.addRow(0, new Label("Product Name"), new Label("Quantity Sold"));

            int row = 1;

            // Iterate through the result set and add each product and quantity to the GridPane
            while (result.next()) {
                String productName = result.getString("prod_name");
                int totalQuantity = result.getInt("total_quantity");

                grid.addRow(row++, new Label(productName), new Label(Integer.toString(totalQuantity)));
            }

            // Create an alert with the GridPane as its content
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Products Sold");
            alert.setHeaderText(null);
            alert.getDialogPane().setContent(grid);

            // Show the alert
            alert.showAndWait();

        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
        } finally {
            // Close the ResultSet, PreparedStatement, and Connection objects to release resources
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showTotalSales() {
        String sqlDay = "SELECT SUM(price) FROM customer WHERE date = ?";
        String sqlWeek = "SELECT SUM(price) FROM customer WHERE date >= ? AND date <= ?";
        String sqlMonth = "SELECT SUM(price) FROM customer WHERE YEAR(date) = YEAR(?) AND MONTH(date) = MONTH(?)";
        String sqlDaysInMonth = "SELECT COUNT(DISTINCT date) FROM customer WHERE YEAR(date) = YEAR(?) AND MONTH(date) = MONTH(?)";

        connect = database.connectDB();

        try {
            float totalDay = 0;
            float totalWeek = 0;
            float totalMonth = 0;
            int daysInMonth = 0;
            float avgSalesPerDay = 0;

            // Current date
            Date currentDate = new Date();
            java.sql.Date sqlCurrentDate = new java.sql.Date(currentDate.getTime());

            // For day
            prepare = connect.prepareStatement(sqlDay);
            prepare.setDate(1, sqlCurrentDate);
            result = prepare.executeQuery();
            if (result.next()) {
                totalDay = result.getFloat(1);
            }

            // For week
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            java.sql.Date weekStartDate = new java.sql.Date(calendar.getTimeInMillis());
            calendar.add(Calendar.DATE, 6); // Move to Saturday
            java.sql.Date weekEndDate = new java.sql.Date(calendar.getTimeInMillis());
            prepare = connect.prepareStatement(sqlWeek);
            prepare.setDate(1, weekStartDate);
            prepare.setDate(2, weekEndDate);
            result = prepare.executeQuery();
            if (result.next()) {
                totalWeek = result.getFloat(1);
            }

            // For month
            calendar.setTime(currentDate);
            java.sql.Date monthDate = new java.sql.Date(calendar.getTimeInMillis());
            prepare = connect.prepareStatement(sqlMonth);
            prepare.setDate(1, monthDate);
            prepare.setDate(2, monthDate);
            result = prepare.executeQuery();
            if (result.next()) {
                totalMonth = result.getFloat(1);
            }

            // Number of days with at least one sale in the current month
            prepare = connect.prepareStatement(sqlDaysInMonth);
            prepare.setDate(1, monthDate);
            prepare.setDate(2, monthDate);
            result = prepare.executeQuery();
            if (result.next()) {
                daysInMonth = result.getInt(1);
            }

            // Calculate average sales per day for the current month
            if (daysInMonth > 0) {
                avgSalesPerDay = totalMonth / daysInMonth;
            }

            // Display sales information
            String message = "Sales Information:\n";
            message += "Today's Sales: ₱" + totalDay + "\n";
            message += "Weekly Sales: ₱" + totalWeek + "\n";
            message += "Monthly Sales: ₱" + totalMonth + "\n";
            message += "Average Sales per Day: ₱" + avgSalesPerDay + "\n";

            JOptionPane.showMessageDialog(null, message, "Sales Information", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    public void dashboardIncomeChart() {
        dashboard_incomeChart.getData().clear();

        String sql = "SELECT date, SUM(total) FROM receipt GROUP BY date ORDER BY TIMESTAMP(date)";
        connect = database.connectDB();
        XYChart.Series chart = new XYChart.Series();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                chart.getData().add(new XYChart.Data<>(result.getString(1), result.getFloat(2)));
            }

            dashboard_incomeChart.getData().add(chart);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dashboardCustomerChart() {
        dashboard_CustomerChart.getData().clear();

        String sql = "SELECT date, COUNT(id) FROM receipt GROUP BY date ORDER BY TIMESTAMP(date)";
        connect = database.connectDB();
        XYChart.Series chart = new XYChart.Series();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                chart.getData().add(new XYChart.Data<>(result.getString(1), result.getInt(2)));
            }

            dashboard_CustomerChart.getData().add(chart);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */
    private ObservableList<productData> cardListData = FXCollections.observableArrayList();

    public void inventoryAddBtn() {
        if (inventory_productID.getText().isEmpty()
                || inventory_productName.getText().isEmpty()
                || inventory_type.getSelectionModel().getSelectedItem() == null
                || inventory_stock.getText().isEmpty()
                || inventory_price.getText().isEmpty()
                || inventory_status.getSelectionModel().getSelectedItem() == null
                || data.path == null) {
            // Error: Empty fields
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
        } else {
            // Validate stock, price, and product ID
            try {
                int stock = Integer.parseInt(inventory_stock.getText());
                double price = Double.parseDouble(inventory_price.getText());
                String prodID = inventory_productID.getText();
                if (stock < 0 || price < 0 || !prodID.matches("\\d+")) {
                    // Error: Invalid input
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid!!!");
                    alert.showAndWait();
                } else {
                    // Proceed with adding the item
                    // CHECK PRODUCT ID
                    String checkProdID = "SELECT prod_id FROM product WHERE prod_id = '"
                            + prodID + "'";
                    connect = database.connectDB();
                    try {
                        statement = connect.createStatement();
                        result = statement.executeQuery(checkProdID);
                        if (result.next()) {
                            // Error: Product ID already exists
                            alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Error Message");
                            alert.setHeaderText(null);
                            alert.setContentText(prodID + " is already taken");
                            alert.showAndWait();
                        } else {
                            // Insert the item into the database
                            String insertData = "INSERT INTO product "
                                    + "(prod_id, prod_name, type, stock, price, status, image, date) "
                                    + "VALUES(?,?,?,?,?,?,?,?)";
                            prepare = connect.prepareStatement(insertData);
                            prepare.setString(1, prodID);
                            prepare.setString(2, inventory_productName.getText());
                            prepare.setString(3, (String) inventory_type.getSelectionModel().getSelectedItem());
                            prepare.setInt(4, stock);
                            prepare.setDouble(5, price);
                            prepare.setString(6, (String) inventory_status.getSelectionModel().getSelectedItem());
                            String path = data.path;
                            path = path.replace("\\", "\\\\");
                            prepare.setString(7, path);
                            // TO GET CURRENT DATE
                            Date date = new Date();
                            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                            prepare.setString(8, String.valueOf(sqlDate));
                            prepare.executeUpdate();
                            // Success message
                            alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Error Message");
                            alert.setHeaderText(null);
                            alert.setContentText("Successfully Added!");
                            alert.showAndWait();
                            // Update table view and clear fields
                            inventoryShowData();
                            inventoryClearBtn();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (NumberFormatException e) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please enter valid numbers for stock and price");
                alert.showAndWait();
            }
        }
    }

    public void menuRemoveBtn() {
        if (getid == 0) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please select the order you want to remove");
            alert.showAndWait();
        } else {
            String selectData = "SELECT prod_id, quantity FROM customer WHERE id = " + getid;
            String deleteData = "DELETE FROM customer WHERE id = " + getid;

            connect = database.connectDB();

            try {
                prepare = connect.prepareStatement(selectData);
                result = prepare.executeQuery();

                if (result.next()) {
                    String prodID = result.getString("prod_id");
                    int qty = result.getInt("quantity");

                    // Remove the order from customer table
                    alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to delete this order?");
                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get().equals(ButtonType.OK)) {
                        // Delete the order
                        prepare = connect.prepareStatement(deleteData);
                        prepare.executeUpdate();

                        // Update product stock
                        String updateStock = "UPDATE product SET stock = stock + ? WHERE prod_id = ?";
                        prepare = connect.prepareStatement(updateStock);
                        prepare.setInt(1, qty);
                        prepare.setString(2, prodID);
                        prepare.executeUpdate();
                    }
                }

                // Refresh order display
                menuShowOrderData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void inventoryImportBtn() {

        FileChooser openFile = new FileChooser();
        openFile.getExtensionFilters().add(new ExtensionFilter("Open Image File", "*png", "*jpg"));

        File file = openFile.showOpenDialog(main_form.getScene().getWindow());

        if (file != null) {

            data.path = file.getAbsolutePath();
            image = new Image(file.toURI().toString(), 138, 143, false, true);

            inventory_imageView.setImage(image);
        }
    }

    public void inventoryClearBtn() {

        inventory_productID.setText("");
        inventory_productName.setText("");
        inventory_type.getSelectionModel().clearSelection();
        inventory_stock.setText("");
        inventory_price.setText("");
        inventory_status.getSelectionModel().clearSelection();
        data.path = "";
        data.id = 0;
        inventory_imageView.setImage(null);

    }

    public ObservableList<productData> menuGetOrder() {
        customerID();
        ObservableList<productData> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM customer WHERE customer_id = " + cID;

        connect = database.connectDB();

        try {

            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            productData prod;

            while (result.next()) {
                prod = new productData(result.getInt("id"),
                        result.getString("prod_id"),
                        result.getString("prod_name"),
                        result.getString("type"),
                        result.getInt("quantity"),
                        result.getDouble("price"),
                        result.getString("image"),
                        result.getDate("date"));
                listData.add(prod);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listData;
    }

    private int getid;

    public void menuSelectOrder() {
        productData prod = menu_tableView.getSelectionModel().getSelectedItem();
        int num = menu_tableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < -1) {
            return;
        }
        // TO GET THE ID PER ORDER
        getid = prod.getId();

    }

    private double totalP;

    public void menuGetTotal() {
        customerID();
        String total = "SELECT SUM(price) FROM customer WHERE customer_id = " + cID;

        connect = database.connectDB();

        try {

            prepare = connect.prepareStatement(total);
            result = prepare.executeQuery();

            if (result.next()) {
                totalP = result.getDouble("SUM(price)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void menuDisplayTotal() {
        menuGetTotal();
        menu_total.setText("₱" + totalP);
    }

    private double amount = 0;
    private double change = 0;

    public void menuAmount() {
        menuGetTotal();

        if (menu_amount.getText().isEmpty() || totalP == 0) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Invalid input or total price is zero.");
            alert.showAndWait();
        } else {
            amount = Double.parseDouble(menu_amount.getText());
            if (amount < totalP) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Insufficient payment. Please provide more.");
                alert.showAndWait();
            } else {
                change = amount - totalP;
                menu_change.setText("₱" + change);
                // Process payment here
            }
        }
    }

    public void menuPayBtn() {

        if (totalP <= 0) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Invalid!");
            alert.showAndWait();
        } else {
            menuGetTotal();
            String insertPay = "INSERT INTO receipt (customer_id, total, date, em_username) "
                    + "VALUES(?,?,?,?)";

            connect = database.connectDB();

            try {
                if (amount <= 0) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid payment amount!");
                    alert.showAndWait();
                } else if (amount < totalP) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Payment is not enough!");
                    alert.showAndWait();
                } else {
                    alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure?");
                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get().equals(ButtonType.OK)) {
                        customerID();
                        menuGetTotal();
                        prepare = connect.prepareStatement(insertPay);
                        prepare.setString(1, String.valueOf(cID));
                        prepare.setString(2, String.valueOf(totalP));

                        Date date = new Date();
                        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                        prepare.setString(3, String.valueOf(sqlDate));
                        prepare.setString(4, data.username);

                        prepare.executeUpdate();

                        // Calculate change
                        double change = amount - totalP;

                        // Retrieve ordered items and their prices
                        StringBuilder orderDetails = new StringBuilder();
                        for (productData item : menuOrderListData) {
                            orderDetails.append(item.getQuantity() + "\t" + item.getProductName() + "\t\t₱" + item.getPrice() + "\n");
                        }

// Create a GridPane to display the order details in a tabular format
                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);

// Add column headers
                        grid.addRow(0, new Label("Quantity"), new Label("Product"), new Label("Price"));

                        int row = 1;

// Iterate through the ordered items and add each item to the GridPane
                        for (productData item : menuOrderListData) {
                            grid.addRow(row++, new Label(String.valueOf(item.getQuantity())), new Label(item.getProductName()), new Label("₱" + String.valueOf(item.getPrice())));
                        }

// Information message
                        Alert infoAlert = new Alert(AlertType.INFORMATION);
                        infoAlert.setTitle("Payment Successful");
                        infoAlert.setHeaderText("Ordered Items and Change");
                        infoAlert.setContentText("Ordered Items:\n\nTotal Payment: ₱" + totalP + "\nPaid: ₱" + amount + "\nChange: ₱" + change);
                        infoAlert.getDialogPane().setContent(grid);
                        infoAlert.showAndWait();

                        menuShowOrderData();
                        menuRestart();

                    } else {
                        alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Information Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Cancelled.");
                        alert.showAndWait();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void menuRestart() {
        totalP = 0;
        change = 0;
        amount = 0;
        menu_total.setText("₱0.0");
        menu_amount.setText("");
        menu_change.setText("₱0.0");
    }

    public void menuRestart1() {
        totalP = 0;
        change = 0;
        amount = 0;
        menu_amount.setText("");
        menu_change.setText("₱0.0");
    }

    private ObservableList<productData> menuOrderListData;

    public void menuShowOrderData() {
        menuOrderListData = menuGetOrder();
        menu_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        menu_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        menu_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        menu_tableView.setItems(menuOrderListData);
    }

    private int cID;

    public void customerID() {

        String sql = "SELECT MAX(customer_id) FROM customer";
        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            if (result.next()) {
                cID = result.getInt("MAX(customer_id)");
            }

            String checkCID = "SELECT MAX(customer_id) FROM receipt";
            prepare = connect.prepareStatement(checkCID);
            result = prepare.executeQuery();
            int checkID = 0;
            if (result.next()) {
                checkID = result.getInt("MAX(customer_id)");
            }

            if (cID == 0) {
                cID += 1;
            } else if (cID == checkID) {
                cID += 1;
            }

            data.cID = cID;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<productData> inventoryDataList() {

        ObservableList<productData> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM product";

        connect = database.connectDB();

        try {

            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            productData prodData;

            while (result.next()) {

                prodData = new productData(result.getInt("id"),
                        result.getString("prod_id"),
                        result.getString("prod_name"),
                        result.getString("type"),
                        result.getInt("stock"),
                        result.getDouble("price"),
                        result.getString("status"),
                        result.getString("image"),
                        result.getDate("date"));

                listData.add(prodData);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    private ObservableList<productData> inventoryListData;

    public void inventoryShowData() {
        inventoryListData = inventoryDataList();

        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));

        inventory_tableView.setItems(inventoryListData);

    }

    private String[] typeList = {"Drinks", "Tea", "Latte", "Foods"};

    public void inventoryTypeList() {

        List<String> typeL = new ArrayList<>();

        for (String data : typeList) {
            typeL.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(typeL);
        inventory_type.setItems(listData);
    }

    private String[] statusList = {"Available", "Unavailable"};

    public void inventoryStatusList() {

        List<String> statusL = new ArrayList<>();

        for (String data : statusList) {
            statusL.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(statusL);
        inventory_status.setItems(listData);

    }

    public ObservableList<productData> menuGetData() {

        String sql = "SELECT * FROM product";
        ObservableList<productData> listData = FXCollections.observableArrayList();
        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            productData prod;

            while (result.next()) {
                prod = new productData(result.getInt("id"),
                        result.getString("prod_id"),
                        result.getString("prod_name"),
                        result.getString("type"),
                        result.getInt("stock"),
                        result.getDouble("price"),
                        result.getString("image"),
                        result.getDate("date"));

                listData.add(prod);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listData;
    }

    public void menuDisplayCard() {

        cardListData.clear();
        cardListData.addAll(menuGetData());

        int row = 0;
        int column = 0;

        menu_gridPane.getChildren().clear();
        menu_gridPane.getRowConstraints().clear();
        menu_gridPane.getColumnConstraints().clear();

        for (int q = 0; q < cardListData.size(); q++) {

            try {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("cardProduct.fxml"));
                AnchorPane pane = load.load();
                CardProductController cardC = load.getController();
                cardC.setData(cardListData.get(q));

                if (column == 2) {
                    column = 0;
                    row += 1;
                }

                menu_gridPane.add(pane, column++, row);

                GridPane.setMargin(pane, new Insets(10));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void switchForm(ActionEvent event) {

        if (event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true);
            inventory_form.setVisible(false);
            menu_form.setVisible(false);
            customers_form.setVisible(false);

            dashboardDisplayNC();
            dashboardDisplayTI();
            dashboardTotalI();
            dashboardNSP();
            inventory_otherBtn.setVisible(true);
            hideButtons();
//            dashboardIncomeChart();
            //        dashboardCustomerChart();
        } else if (event.getSource() == inventory_btn) {
            dashboard_form.setVisible(false);
            inventory_form.setVisible(true);
            menu_form.setVisible(false);
            customers_form.setVisible(false);

            inventoryTypeList();
            inventoryStatusList();
            inventoryShowData();

        } else if (event.getSource() == menu_btn) {
            dashboard_form.setVisible(false);
            inventory_form.setVisible(false);
            menu_form.setVisible(true);
            customers_form.setVisible(false);
            inventory_otherBtn.setVisible(true);
            hideButtons();

            menuDisplayCard();
            menuDisplayTotal();
            menuShowOrderData();
            menuRestart1();

        } else if (event.getSource() == customers_btn) {
            dashboard_form.setVisible(false);
            inventory_form.setVisible(false);
            menu_form.setVisible(false);
            customers_form.setVisible(true);
            inventory_otherBtn.setVisible(true);

            customersShowData();
            hideButtons();
        }

    }

    public void logout() {

        try {

            alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(ButtonType.OK)) {

                // TO HIDE MAIN FORM 
                logout_btn.getScene().getWindow().hide();

                // LINK YOUR LOGIN FORM AND SHOW IT 
                Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

                Stage stage = new Stage();
                Scene scene = new Scene(root);

                stage.setTitle("Ong cha-a Management System");

                stage.setScene(scene);
                stage.show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void displayUsername() {

        String user = data.username;
        user = user.substring(0, 1).toUpperCase() + user.substring(1);
        user += "!";
        username.setText(user);

    }

    public void inventorySelectData() {

        productData prodData = inventory_tableView.getSelectionModel().getSelectedItem();
        int num = inventory_tableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < -1) {
            return;
        }

        inventory_productID.setText(prodData.getProductId());
        inventory_productName.setText(prodData.getProductName());
        inventory_stock.setText(String.valueOf(prodData.getStock()));
        inventory_price.setText(String.valueOf(prodData.getPrice()));

        data.path = prodData.getImage();

        String path = "File:" + prodData.getImage();
        data.date = String.valueOf(prodData.getDate());
        data.id = prodData.getId();

        image = new Image(path, 120, 127, false, true);
        inventory_imageView.setImage(image);
    }

    public void inventoryUpdateBtn() {

        if (inventory_productID.getText().isEmpty()
                || inventory_productName.getText().isEmpty()
                || inventory_type.getSelectionModel().getSelectedItem() == null
                || inventory_stock.getText().isEmpty()
                || inventory_price.getText().isEmpty()
                || inventory_status.getSelectionModel().getSelectedItem() == null
                || data.path == null || data.id == 0) {

            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();

        } else {

            int idValue = Integer.parseInt(inventory_productID.getText());
            int stockValue = Integer.parseInt(inventory_stock.getText());
            double priceValue = Double.parseDouble(inventory_price.getText());

            // NEGATIVE UPDATE STOCK OKS NA
            if (stockValue < 0 || priceValue < 0 || idValue < 0) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText(null);
                alert.setContentText("Please enter positive values!");
                alert.showAndWait();
                return;
            }

            String path = data.path;
            path = path.replace("\\", "\\\\");

            String updateData = "UPDATE product SET "
                    + "prod_id = '" + inventory_productID.getText() + "', prod_name = '"
                    + inventory_productName.getText() + "', type = '"
                    + inventory_type.getSelectionModel().getSelectedItem() + "', stock = '"
                    + inventory_stock.getText() + "', price = '"
                    + inventory_price.getText() + "', status = '"
                    + inventory_status.getSelectionModel().getSelectedItem() + "', image = '"
                    + path + "', date = '"
                    + data.date + "' WHERE id = " + data.id;

            connect = database.connectDB();

            try {

                alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirm?");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to UPDATE PRoduct ID: " + inventory_productID.getText() + "?");
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get().equals(ButtonType.OK)) {
                    prepare = connect.prepareStatement(updateData);
                    prepare.executeUpdate();

                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Update");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Updated!");
                    alert.showAndWait();

                    // TO UPDATE YOUR TABLE VIEW
                    inventoryShowData();
                    // TO CLEAR YOUR FIELDS
                    inventoryClearBtn();
                } else {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Cancel");
                    alert.setHeaderText(null);
                    alert.setContentText("Cancelled.");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void inventoryDeleteBtn() {
        if (data.id == 0) {

            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();

        } else {
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to DELETE Product ID: " + inventory_productID.getText() + "?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(ButtonType.OK)) {
                String deleteData = "DELETE FROM product WHERE id = " + data.id;
                try {
                    prepare = connect.prepareStatement(deleteData);
                    prepare.executeUpdate();

                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("successfully Deleted!");
                    alert.showAndWait();

                    // TO UPDATE YOUR TABLE VIEW
                    inventoryShowData();
                    // TO CLEAR YOUR FIELDS
                    inventoryClearBtn();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Cancelled");
                alert.showAndWait();
            }
        }
    }

    public ObservableList<customersData> customersDataList() {

        ObservableList<customersData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM receipt";
        connect = database.connectDB();

        try {

            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            customersData cData;

            while (result.next()) {
                cData = new customersData(result.getInt("id"),
                        result.getInt("customer_id"),
                        result.getDouble("total"),
                        result.getDate("date"),
                        result.getString("em_username"));

                listData.add(cData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    private ObservableList<customersData> customersListData;

    public void customersShowData() {
        customersListData = customersDataList();

        customers_col_customerID.setCellValueFactory(new PropertyValueFactory<>("customersID"));
        customers_col_total.setCellValueFactory(new PropertyValueFactory<>("total"));
        customers_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        customers_col_cashier.setCellValueFactory(new PropertyValueFactory<>("emUsername"));

        customers_tableView.setItems(customersListData);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // TODO
        displayUsername();

        dashboardDisplayNC();
        dashboardDisplayTI();
        dashboardTotalI();
        dashboardNSP();

        inventoryTypeList();
        inventoryStatusList();
        inventoryShowData();

        menuDisplayCard();
        menuGetOrder();
        menuDisplayTotal();
        menuShowOrderData();

        customersShowData();
        //hideButtons();

    }

}
