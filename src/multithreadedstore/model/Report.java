package multithreadedstore.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents the summary of processed orders.
 */
public record Report(long totalOrders, double totalProfit, List<String> top3Products, long totalReservations,
                     Map<Product, Integer> maxReservedByProduct) {
    public Report(long totalOrders, double totalProfit, List<String> top3Products, long totalReservations, Map<Product, Integer> maxReservedByProduct) {
        this.totalOrders = totalOrders;
        this.totalProfit = totalProfit;
        this.top3Products = Collections.unmodifiableList(top3Products);
        this.totalReservations = totalReservations;
        this.maxReservedByProduct = maxReservedByProduct;
    }
}
