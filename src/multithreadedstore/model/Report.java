package multithreadedstore.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Summary of processed orders and related statistics.
 *
 * @param totalOrders total number of orders processed
 * @param totalProfit total profit across all orders
 * @param totalReservations number of reservation orders
 * @param totalCancellations number of reservation cancellations
 * @param top3Products names of top 3 most ordered products
 * @param maxReservedByProduct maximum reserved quantity per product
 */
public record Report(
        long totalOrders,
        double totalProfit,
        long totalReservations,
        long totalCancellations,
        List<String> top3Products,
        Map<Product, Integer> maxReservedByProduct
) {
    public Report {
        top3Products = Collections.unmodifiableList(top3Products);
        maxReservedByProduct = Collections.unmodifiableMap(maxReservedByProduct);
    }
}

