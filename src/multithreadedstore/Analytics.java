package multithreadedstore;

import java.util.*;
import java.util.stream.Collectors;

class Analytics {
    static void report(List<Order> processed) {
        long totalOrders = processed.size();
        double profit = processed.parallelStream()
                .flatMap(o -> o.items().entrySet().stream())
                .mapToDouble(e -> e.getKey().price() * e.getValue())
                .sum();

        var sales = processed.parallelStream()
                .flatMap(o -> o.items().entrySet().stream())
                .collect(Collectors.groupingBy(
                        e -> e.getKey().name(),
                        Collectors.summingLong(Map.Entry::getValue)));

        var top3 = sales.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        System.out.println("Total orders: " + totalOrders);
        System.out.println("Total profit: " + profit);
        System.out.println("Top 3: " + top3);
    }
}
