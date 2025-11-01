package multithreadedstore.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents the summary of processed orders.
 */
public record Report(long totalOrders, double totalProfit, List<String> top3Products) {
    public Report(long totalOrders, double totalProfit, List<String> top3Products) {
        this.totalOrders = totalOrders;
        this.totalProfit = totalProfit;
        this.top3Products = Collections.unmodifiableList(top3Products);
    }
}
