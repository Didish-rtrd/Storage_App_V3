import java.util.List;

public class Order {
    int id;
    String status;
    String currency;
    String total;
    Billing billing;
    List<LineItem> line_items;
}
