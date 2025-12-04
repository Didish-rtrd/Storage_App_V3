
/*
 String consumerKey = "ck_4af2b15d378c6733f7a775841665144f21764b1f";
 String consumerSecret = "cs_db4985a734127fb6e67fd82a0986abff81610559";
 Odkaz na prouct JSON včetně klíčů
 https://eshop-example.infinityfreeapp.com/wp-json/wc/v3/products?consumer_key=ck_4af2b15d378c6733f7a775841665144f21764b1f&consumer_secret=cs_db4985a734127fb6e67fd82a0986abff81610559
*/

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Main extends Application {

    private List<Order> allOrders; //načte všechny objednávky
    private int orderIndex = 0; //index aktuální objednávky

    private TableView<LineItem> table = new TableView<>();

    public void start(Stage stage) {
        Label header = new Label("Informace o objednávce");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblCustomer = new Label("Zákazník: ");
        Label lblTotal = new Label("Celkem: ");
        Label lblStatus = new Label("Status: ");
        Label lblCurrency = new Label("Měna: ");
        Label lblID = new Label("ID objednávky: ");

        // Tabulka produktů
        TableColumn<LineItem, String> colName = new TableColumn<>("Produkt");
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().name));

        TableColumn<LineItem, String> colQty = new TableColumn<>("Množství");
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().quantity)));

        TableColumn<LineItem, String> colPrice = new TableColumn<>("Cena (Kč)");
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().total));

        table.getColumns().addAll(colName, colQty, colPrice);

        Button btnLoad = new Button("Načíst objednávku");
        btnLoad.setOnAction(e -> {
            allOrders = loadOrderFromApi();  // teď vrací List<Order>
            orderIndex = 0;               // začneme první objednávkou
        });

        Button btnLoadAll = new Button("Načíst všechny objednávky");
        btnLoadAll.setOnAction(e -> {
            allOrders = loadOrderFromApi();
            orderIndex = 0; // první objednávka
            showOrder(orderIndex, lblCustomer, lblTotal, lblStatus, lblCurrency, lblID);
        });

        Button btnPrev = new Button("Předchozí");
        btnPrev.setOnAction(e -> {
            if (orderIndex < allOrders.size() - 1) {
                orderIndex++;
                showOrder(orderIndex, lblCustomer, lblTotal, lblStatus, lblCurrency, lblID);
            }
        });

        Button btnNext = new Button("Další");
        btnNext.setOnAction(e -> {
            if (orderIndex > 0) {
                orderIndex--;
                showOrder(orderIndex, lblCustomer, lblTotal, lblStatus, lblCurrency, lblID);
            }
        });

        VBox infoBox = new VBox(5, lblCustomer, lblTotal, lblStatus, lblCurrency, lblID);
        infoBox.setStyle("-fx-font-size: 14px;");

        HBox nav = new HBox(10, btnPrev, btnNext);
        VBox root = new VBox(15, header, btnLoadAll, nav, infoBox, table);

        root.setPadding(new javafx.geometry.Insets(15));
        Scene scene = new Scene(root, 1000, 750);
        stage.setTitle("WooCommerce objednávka – JavaFX demo");
        stage.setScene(scene);
        stage.show();
    }

    private List<Order> loadOrderFromApi() {
        try {
            String apiUrl = "https://eshop-example.infinityfreeapp.com/wp-json/wc/v3/orders"
                    + "?consumer_key=ck_4af2b15d378c6733f7a775841665144f21764b1f"
                    + "&consumer_secret=cs_db4985a734127fb6e67fd82a0986abff81610559";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            System.out.println("HTTP Response Code: " + connection.getResponseCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Order>>() {}.getType();

            return gson.fromJson(response.toString(), listType);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Nepodařilo se načíst objednávku").show();
            return null;
        }
    }
    private void showOrder(int index, Label lblCustomer, Label lblTotal, Label lblStatus, Label lblCurrency, Label lblID) {
        if (allOrders == null || allOrders.isEmpty()) return;

        Order order = allOrders.get(index);

        lblCustomer.setText("Zákazník: " + order.billing.first_name + " " + order.billing.last_name + " | " +  order.billing.email);
        lblTotal.setText("Celkem: " + order.total + " Kč");
        lblStatus.setText("Status: " + order.status);
        lblCurrency.setText("Měna: " + order.currency);
        lblID.setText("ID objednávky: " + order.id);

        table.getItems().setAll(order.line_items);
    }

    public static void main(String[] args) {
        launch();
    }
}
