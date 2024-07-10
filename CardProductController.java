package cofeeshop;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 */
public class CardProductController implements Initializable {

    @FXML
    private ComboBox<String> card_size;

    @FXML
    private ComboBox<String> sugar_level;

    @FXML
    private AnchorPane card_form;

    @FXML
    private Label prod_name;

    @FXML
    private Label prod_price;

    @FXML
    private ImageView prod_imageView;

    @FXML
    private Spinner<Integer> prod_spinner;

    @FXML
    private Button prod_addBtn;

    private productData prodData;
    private Image image;

    private String prodID;
    private String type;
    private String prod_date;
    private String prod_image;

    private SpinnerValueFactory<Integer> spin;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;

    private double basePrice;  // Base price of the product
    private double pr;         // Adjusted price of the product

    private String[] sizeList = {"Large", "Medium", "Small"};
    private String[] sugarList = {"10%", "25%", "50%", "75%", "100%"};

    public void setData(productData prodData) {
        this.prodData = prodData;

        prod_image = prodData.getImage();
        prod_date = String.valueOf(prodData.getDate());
        type = prodData.getType();
        prodID = prodData.getProductId();
        prod_name.setText(prodData.getProductName());
        basePrice = prodData.getPrice();
        prod_price.setText("₱" + String.valueOf(basePrice));
        String path = "File:" + prodData.getImage();
        image = new Image(path, 286, 176, false, true);
        prod_imageView.setImage(image);
        pr = basePrice;

        // Show or hide the sugar level ComboBox based on the product type
        if (type.equalsIgnoreCase("drinks") || type.equalsIgnoreCase("latte") || type.equalsIgnoreCase("tea")) {
            sugar_level.setVisible(true);
            card_size.setVisible(true);
        } else {
            sugar_level.setVisible(false);
            card_size.setVisible(false);
        }
    }

    public void cardSizeList() {
        List<String> cardSize = new ArrayList<>();
        for (String data : sizeList) {
            cardSize.add(data);
        }
        ObservableList<String> listData = FXCollections.observableArrayList(cardSize);
        card_size.setItems(listData);
        card_size.setOnAction(event -> handleSizeSelection());

        List<String> sugarLevels = new ArrayList<>();
        for (String level : sugarList) {
            sugarLevels.add(level);
        }
        ObservableList<String> sugarData = FXCollections.observableArrayList(sugarLevels);
        sugar_level.setItems(sugarData);
    }

    @FXML
    private void handleSizeSelection() {
        String selectedSize = card_size.getValue();
        if (selectedSize != null) {
            setSize(selectedSize);
        }
    }

    public void setSize(String size) {
        double adjustedPrice = basePrice;
        switch (size.toLowerCase()) {
            case "large":
                adjustedPrice += 10;
                break;
            case "medium":
                adjustedPrice += 5;
                break;
            case "small":
                // Price remains the same for small
                break;
            default:
                throw new IllegalArgumentException("Invalid size: " + size);
        }
        pr = adjustedPrice;
        prod_price.setText("₱" + String.valueOf(adjustedPrice));
    }

    private int qty;

    public void setQuantity() {
        spin = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 0);
        prod_spinner.setValueFactory(spin);
    }

    private double totalP;

    public void addBtn() {
        mainFormController mForm = new mainFormController();
        mForm.customerID();

        qty = prod_spinner.getValue();
        String check = "";
        String checkAvailable = "SELECT status FROM product WHERE prod_id = '" + prodID + "'";

        connect = database.connectDB();

        try {
            int checkStck = 0;
            String checkStock = "SELECT stock FROM product WHERE prod_id = '" + prodID + "'";

            prepare = connect.prepareStatement(checkStock);
            result = prepare.executeQuery();

            if (result.next()) {
                checkStck = result.getInt("stock");
            }

            if (checkStck == 0) {
                String updateStock = "UPDATE product SET prod_name = '"
                        + prod_name.getText() + "', type = '"
                        + type + "', stock = 0, price = " + basePrice
                        + ", status = 'Unavailable', image = '"
                        + prod_image + "', date = '"
                        + prod_date + "' WHERE prod_id = '"
                        + prodID + "'";
                prepare = connect.prepareStatement(updateStock);
                prepare.executeUpdate();
            }

            prepare = connect.prepareStatement(checkAvailable);
            result = prepare.executeQuery();

            if (result.next()) {
                check = result.getString("status");
            }

            // Check if sugar level and size are selected
            String sugarLevel = (sugar_level.isVisible() && sugar_level.getValue() != null) ? sugar_level.getValue() : "";
            String size = (card_size.isVisible() && card_size.getValue() != null) ? card_size.getValue() : ""; // Assuming size_combo is your size dropdown
            if (type.equalsIgnoreCase("drinks") || type.equalsIgnoreCase("latte") || type.equalsIgnoreCase("tea")) {
                if (sugarLevel.isEmpty()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select a sugar level!");
                    alert.showAndWait();
                    return;
                }

                if (size.isEmpty()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select size!");
                    alert.showAndWait();
                    return;
                }
            }

            if (!check.equals("Available") || qty == 0) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong!");
                alert.showAndWait();
            } else {
                if (checkStck < qty) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid! \nThis product is out of stock.");
                    alert.showAndWait();
                } else {
                    prod_image = prod_image.replace("\\", "\\\\");

                    String insertData = "INSERT INTO customer "
                            + "(customer_id, prod_id, prod_name, type, quantity, price, date, image, em_username, sugar_level, size) "
                            + "VALUES(?,?,?,?,?,?,?,?,?,?,?)"; // Updated for size
                    prepare = connect.prepareStatement(insertData);
                    prepare.setString(1, String.valueOf(data.cID));
                    prepare.setString(2, prodID);
                    prepare.setString(3, prod_name.getText());
                    prepare.setString(4, type);
                    prepare.setString(5, String.valueOf(qty));

                    totalP = (qty * pr);
                    prepare.setString(6, String.valueOf(totalP));

                    Date date = new Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                    prepare.setString(7, String.valueOf(sqlDate));

                    prepare.setString(8, prod_image);
                    prepare.setString(9, data.username);
                    prepare.setString(10, sugarLevel);
                    prepare.setString(11, size); // Adding size to the insert statement

                    prepare.executeUpdate();

                    int upStock = checkStck - qty;

                    String updateStock = "UPDATE product SET prod_name = '"
                            + prod_name.getText() + "', type = '"
                            + type + "', stock = " + upStock + ", price = " + basePrice
                            + ", status = '"
                            + check + "', image = '"
                            + prod_image + "', date = '"
                            + prod_date + "' WHERE prod_id = '"
                            + prodID + "'";

                    prepare = connect.prepareStatement(updateStock);
                    prepare.executeUpdate();

                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Added!");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setQuantity();
        cardSizeList();
    }
}
