package multithreadedstore;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class Warehouse {
    private final ConcurrentHashMap<Product, Integer> stock = new ConcurrentHashMap<>();

    Warehouse(List<Product> products) {
        products.forEach(p -> stock.put(p, 10));
    }

    void process(Order order) {
        order.items().forEach((p, q) -> stock.computeIfPresent(p, (k, v) -> v - q));
    }
}
