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
    public boolean process(Order order) {
        if (order == null || order.getItems().isEmpty()) {
            return false;
        }

        for (var entry : order.getItems().entrySet()) {
            var product = entry.getKey();
            var quantity = entry.getValue();

            Integer currentStock = stock.get(product);
            if (currentStock == null || currentStock < quantity) {
                return false;
            }
        }

        order.getItems().forEach((product, quantity) ->
                stock.computeIfPresent(product, (key, currentStock) -> currentStock - quantity)
        );

        return true;
    }

}
