package multithreadedstore.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a warehouse holding product stock and processing orders.
 */
public class Warehouse {

    private final ConcurrentHashMap<Product, Integer> stock = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Product, Integer> reservedStock = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Product, Integer> getMaxReservedByProduct = new ConcurrentHashMap<>();

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
     * Returns Map of maximum reserved product quantities per Product.
     */
    public ConcurrentHashMap<Product, Integer> getMaxReservedByProduct() {
        return getMaxReservedByProduct;
    }

    /**
     * Processes an order, reducing the stock of each product accordingly.
     * Order can not process if it is empty or more is ordered than in stock. In that case return false
     * Return true if order processed.
     *
     * @param order the order to process
     */
    public boolean process(Order order) {
        if (order == null || order.getItems().isEmpty()) {
            return false;
        }

        if(!hasEnoughStock(order)) {
            return false;
        }

        order.getItems().forEach((product, quantity) ->
                stock.computeIfPresent(product, (key, currentStock) -> currentStock - quantity)
        );

        return true;
    }

    /**
     * Reserve an order, reducing the stock of each product accordingly. Increase the reserved stock and update
     * maxReservedByProduct for analytics.
     *
     * Order can not be reserved if it is empty or more is reserved than in stock. In that case return false.
     * Return true if order Reserved successfully.
     *
     * @param order the order to process
     */
    public boolean reserveProduct(Order reservation) {
        if (reservation == null || reservation.getItems().isEmpty()) {
            return false;
        }

        if(!hasEnoughStock(reservation)) {
            return false;
        }

        reservation.getItems().forEach((product, quantity) -> {
            stock.computeIfPresent(product, (key, currentStock) -> currentStock - quantity);
            int reservedQty = reservedStock.merge(product, quantity, Integer::sum);
            getMaxReservedByProduct.merge(product, reservedQty, Math::max);
        });

        return true;
    }

    /**
     * Cancels a reservation for the given products and quantities.
     * Restores stock and reduces reserved quantities if enough were reserved.
     *
     * @param reservation the reservation to cancel
     * @return true if cancellation succeeded, false if not enough reserved
     */
    public boolean cancelReservation(Order reservation) {
        if (reservation == null || reservation.getItems().isEmpty() || !hasEnoughReserved(reservation)) {
            return false;
        }

        for (var entry : reservation.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            int reservedQtyAfterCancel = reservedStock.computeIfPresent(product, (key, reservedQty) -> reservedQty - quantity);
            getMaxReservedByProduct.merge(product, reservedQtyAfterCancel, Math::max);

            stock.merge(product, quantity, Integer::sum);
        }

        return true;
    }

    /**
     * Purchases products from reserved stock.
     * Reduces reserved quantity but does not modify normal stock.
     *
     * @param reservation the reservation to check out from reserve stock.
     * @return true if checkout succeeded, false if not enough reserved.
     */
    public boolean checkoutReservation(Order reservation) {
        if (reservation == null || reservation.getItems().isEmpty() || !hasEnoughReserved(reservation)) {
            return false;
        }

        for (var entry : reservation.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            reservedStock.computeIfPresent(product, (key, reservedQty) -> reservedQty - quantity);
        }

        return true;
    }

    /**
     * Check if all Products in an Order is available in the warehouse stock.
     *
     * @param order order to check in the stock.
     * @return true if all products in order are available.
     */
    private boolean hasEnoughStock(Order order) {
        for (var entry : order.getItems().entrySet()) {
            var product = entry.getKey();
            var quantity = entry.getValue();
            Integer currentStock = stock.get(product);
            if (currentStock == null || currentStock < quantity) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all Products in an Order is available in the warehouse reserved stock.
     *
     * @param reservation order to check in the reserve stock.
     * @return true if all products in order are available.
     */
    private boolean hasEnoughReserved(Order reservation) {
        for (var entry : reservation.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            if (reservedStock.getOrDefault(product, 0) < quantity) {
                return false;
            }
        }
        return true;
    }
}
