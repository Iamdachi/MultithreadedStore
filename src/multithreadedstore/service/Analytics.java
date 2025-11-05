package multithreadedstore.service;

import multithreadedstore.model.Order;
import multithreadedstore.model.Report;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides analytics reports on processed orders.
 */
public class Analytics {

    /**
     * Generates a report for a list of processed orders.
     *
     * @param processed the list of processed orders
     * @return the analytics report
     */
    public static Report generateReport(List<Order> processed, List<Order> reservedOrders) {
        if (processed == null || processed.isEmpty() || reservedOrders == null || reservedOrders.isEmpty()) {
            return new Report(0, 0.0, Collections.emptyList(), 0);
        }

        long totalOrders = processed.size();

        long totalReservations = reservedOrders.size();

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

        return new Report(totalOrders, totalProfit, top3Products, totalReservations);
    }
}
