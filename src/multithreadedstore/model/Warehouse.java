package multithreadedstore.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a warehouse holding product stock and processing orders.
 */
public class Warehouse {

    private final ConcurrentHashMap<Product, Integer> stock = new ConcurrentHashMap<>();

    /**
     * Initializes the warehouse with a list of products, each starting with default quantity.
     *
     * @param products the list of products to stock
     */
    public Warehouse(List<Product> products) {
        if (products != null) {
            products.forEach(product -> stock.put(product, 10));
        }
    }

    /**
     * Processes an order, reducing the stock of each product accordingly.
     *
     * @param order the order to process
     */
    public void process(Order order) {
        if (order == null || order.getItems().isEmpty()) {
            return;
        }

        order.getItems().forEach((product, quantity) -> {
            stock.computeIfPresent(product, (key, currentStock) -> {
                int updated = currentStock - quantity;
                return Math.max(updated, 0);
            });
        });
    }

}
