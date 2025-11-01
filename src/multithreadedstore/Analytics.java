package multithreadedstore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides analytics reports on processed orders.
 */
public class Analytics {

    /**
     * Represents the summary of processed orders.
     */
    public static class Report {
        private final long totalOrders;
        private final double totalProfit;
        private final List<String> top3Products;

        public Report(long totalOrders, double totalProfit, List<String> top3Products) {
            this.totalOrders = totalOrders;
            this.totalProfit = totalProfit;
            this.top3Products = Collections.unmodifiableList(top3Products);
        }

        public long getTotalOrders() {
            return totalOrders;
        }

        public double getTotalProfit() {
            return totalProfit;
        }

        public List<String> getTop3Products() {
            return top3Products;
        }
    }

    /**
     * Generates a report for a list of processed orders.
     *
     * @param processed the list of processed orders
     * @return the analytics report
     */
    public static Report generateReport(List<Order> processed) {
        if (processed == null || processed.isEmpty()) {
            return new Report(0, 0.0, Collections.emptyList());
        }

        long totalOrders = processed.size();

        double totalProfit = processed.parallelStream()
                .flatMap(order -> order.getItems().entrySet().stream())
                .mapToDouble(entry -> entry.getKey().price() * entry.getValue())
                .sum();

        Map<String, Long> salesByProduct = processed.parallelStream()
                .flatMap(order -> order.getItems().entrySet().stream())
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().name(),
                        Collectors.summingLong(Map.Entry::getValue)));

        List<String> top3Products = salesByProduct.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        return new Report(totalOrders, totalProfit, top3Products);
    }
}
