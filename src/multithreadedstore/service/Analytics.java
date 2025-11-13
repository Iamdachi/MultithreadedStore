package multithreadedstore.service;

import multithreadedstore.model.Order;
import multithreadedstore.model.Product;
import multithreadedstore.model.Report;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides analytics and summary reports for processed, reserved,
 * and cancelled orders.
 */
public class Analytics {

    /**
     * Generates a summarized report of all processedOrders orders, including
     * profit, reservation, cancellation counts, and top-selling products.
     *
     * @param processedOrders     list of successfully processedOrders (purchased) orders
     * @param reservedOrders      list of orders that reserved products
     * @param cancelledOrders     list of cancelled reservation orders
     * @param maxReservedByProduct map tracking the maximum reserved quantity per product
     * @return a {@link Report} containing total orders, profit, reservations, cancellations,
     *         top three products by sales volume, and maximum reserved quantities
     */
    public static Report generateReport(
            List<Order> processedOrders,
            List<Order> reservedOrders,
            List<Order> cancelledOrders,
            Map<Product, Integer> maxReservedByProduct
    ) {
        if (processedOrders == null || processedOrders.isEmpty() || reservedOrders == null || reservedOrders.isEmpty()) {
            return new Report(0, 0.0, 0, 0, Collections.emptyList(), maxReservedByProduct);
        }

        long totalOrders = processedOrders.size();

        long totalReservations = reservedOrders.size();

        long totalCancellations = cancelledOrders.size();

        double totalProfit = processedOrders.parallelStream()
                .flatMap(order -> order.getItems().entrySet().stream())
                .mapToDouble(entry -> entry.getKey().price() * entry.getValue())
                .sum();

        Map<String, Long> salesByProduct = processedOrders.parallelStream()
                .flatMap(order -> order.getItems().entrySet().stream())
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().name(),
                        Collectors.summingLong(Map.Entry::getValue)));

        List<String> top3Products = salesByProduct.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        return new Report(totalOrders, totalProfit, totalReservations,totalCancellations, top3Products, maxReservedByProduct);
    }
}
