package multithreadedstore.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an order containing products and their quantities.
 * Can also act as a poison pill to signal termination in multithreaded queues.
 * Includes different methods to distinguish between different types of orders.
 */
public class Order {

    /** Special order used to signal termination in queues. */
    public static final Order POISON = new Order(true);

    private final Map<Product, Integer> items;
    final boolean poison;

    /**
     * Creates a regular empty order.
     */
    public Order() {
        this(false);
    }

    /**
     * Creates an order.
     *
     * @param poison true if this order is a poison pill, false otherwise
     */
    private Order(boolean poison) {
        this.items = new HashMap<>();
        this.poison = poison;
    }

    /**
     * Adds a product and its quantity to the order.
     *
     * @param product the product to add
     * @param quantity the quantity of the product
     * @throws IllegalArgumentException if quantity is negative
     */
    public void add(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        items.put(product, quantity);
    }

    /**
     * Returns an unmodifiable view of the items in this order.
     */
    public Map<Product, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Checks whether this order is a poison pill.
     */
    public boolean isPoison() {
        return poison;
    }

    /**
     * Checks whether this order is a reservation.
     */
    public boolean isReservationOrder() {
        return false;
    }

    /**
     * Indicates this order is not an order from reserved stock.
     */
    public boolean isReservationCheckoutOrder() {
        return false;
    }

    /**
     * Indicates this order is not a reserve cancellation order from reserved stock.
     */
    public boolean isReservationCancellationOrder() {
        return false;
    }
}
